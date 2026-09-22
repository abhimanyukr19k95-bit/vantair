package com.vantair.api.service;

import com.vantair.api.config.ApiException;
import com.vantair.api.dto.Dtos.CartItemRequest;
import com.vantair.api.dto.Dtos.PlaceOrderRequest;
import com.vantair.api.model.Address;
import com.vantair.api.model.Coupon;
import com.vantair.api.model.Order;
import com.vantair.api.model.OrderItem;
import com.vantair.api.model.OrderStatus;
import com.vantair.api.model.Product;
import com.vantair.api.model.ProductVariant;
import com.vantair.api.model.RefundCoupon;
import com.vantair.api.model.ShippingAddress;
import com.vantair.api.model.User;
import com.vantair.api.repository.OrderRepository;
import com.vantair.api.repository.ProductRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final AtomicLong SEQUENCE = new AtomicLong();

    private final OrderRepository orders;
    private final ProductRepository products;
    private final UserService userService;
    private final CouponService couponService;
    private final RefundCouponService refundCouponService;
    private final DeliveryService deliveryService;
    private final OtpService otpService;

    public OrderService(OrderRepository orders, ProductRepository products, UserService userService,
                        CouponService couponService, RefundCouponService refundCouponService,
                        DeliveryService deliveryService, OtpService otpService) {
        this.orders = orders;
        this.products = products;
        this.userService = userService;
        this.couponService = couponService;
        this.refundCouponService = refundCouponService;
        this.deliveryService = deliveryService;
        this.otpService = otpService;
    }

    @Transactional
    public Order placeOrder(PlaceOrderRequest req) {
        User user = userService.get(req.userId());
        Address address = user.getAddresses().stream()
                .filter(a -> a.getId().equals(req.addressId()))
                .findFirst()
                .orElseThrow(() -> ApiException.badRequest("Selected address does not belong to this user."));

        if (req.items() == null || req.items().isEmpty()) {
            throw ApiException.badRequest("Cart is empty.");
        }

        Order order = new Order();
        order.setId(generateOrderId());
        order.setUserId(user.getId());
        order.setAddress(snapshot(address));
        order.setPayment(normalizePayment(req.payment()));

        int subtotal = 0;
        for (CartItemRequest line : req.items()) {
            Product product = products.findById(line.productId())
                    .orElseThrow(() -> ApiException.badRequest("Unknown product: " + line.productId()));
            if (!product.isActive()) {
                throw ApiException.badRequest("Product unavailable: " + product.getName());
            }
            int price = priceFor(product, line.variantIndex());
            String variantLabel = variantLabel(product, line.variantIndex());

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setVariantIndex(line.variantIndex());
            item.setVariantLabel(variantLabel);
            item.setEmoji(product.getEmoji());
            item.setPrice(price);
            item.setQty(line.qty());
            order.addItem(item);

            subtotal += price * line.qty();
        }

        boolean express = "express".equalsIgnoreCase(req.deliveryType())
                || "express2".equalsIgnoreCase(req.deliveryType());

        // Coupon handling — recompute server-side, never trust client amounts.
        int discount = 0;
        boolean freeShipping = false;
        Long redeemedRefundId = null;
        Optional<Coupon> coupon = couponService.findActive(req.couponCode());
        if (coupon.isPresent()) {
            Coupon c = coupon.get();
            if (c.getMinOrder() == null || subtotal >= c.getMinOrder()) {
                if ("shipping".equals(c.getType())) {
                    freeShipping = true;
                } else {
                    discount = couponService.computeDiscount(c, subtotal);
                }
                order.setCouponCode(c.getCode());
            }
        } else {
            // Not a standard coupon — try a refund coupon belonging to this customer.
            Optional<RefundCoupon> refund =
                    refundCouponService.findRedeemable(req.couponCode(), user.getId());
            if (refund.isPresent()) {
                RefundCoupon rc = refund.get();
                discount = Math.min(rc.getValue() == null ? 0 : rc.getValue(), subtotal);
                order.setCouponCode(rc.getCode());
                redeemedRefundId = rc.getId();
            }
        }

        // Free-shipping threshold is based on the order subtotal (pre-discount),
        // matching the storefront — a coupon should not push the cart back below
        // the free-shipping bar and re-add a delivery charge.
        int delivery = freeShipping ? 0 : deliveryService.deliveryCharge(subtotal, express);
        int codFee = "cod".equals(order.getPayment()) ? DeliveryService.COD_CHARGE : 0;
        int total = Math.max(0, subtotal - discount) + delivery + codFee;

        // If a coupon covers the entire payable amount, the customer pays nothing —
        // record the payment mode as the coupon itself rather than the unused UPI/COD.
        if (total == 0 && order.getCouponCode() != null) {
            order.setPayment("coupon");
        }

        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setDelivery(delivery);
        order.setCodFee(codFee);
        order.setTotal(total);

        // Delivery OTP
        String otp = otpService.generateOtp();
        order.setOtp(otp);
        order.setOtpHash(otpService.hash(otp, order.getId()));

        order.setStatus(OrderStatus.Confirmed);
        order.setDeliveryPartner("BlueDart");
        order.setTrackingId("BD" + lastDigits(System.currentTimeMillis(), 8));
        order.setExpectedDelivery(deliveryService.expectedDelivery(
                address.getPincode(), express, "express2".equalsIgnoreCase(req.deliveryType()) ? "Next Day" : "Same Day"));

        Order saved = orders.save(order);

        // Burn the refund coupon only after the order is persisted.
        if (redeemedRefundId != null) {
            refundCouponService.markUsed(redeemedRefundId);
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public Order get(String id) {
        return orders.findById(id)
                .orElseThrow(() -> ApiException.notFound("Order not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Order> listForUser(Long userId) {
        return orders.findByUserIdOrderByPlacedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Order> listAll() {
        return orders.findAllByOrderByPlacedAtDesc();
    }

    @Transactional
    public Order updateStatus(String id, String status) {
        Order order = get(id);
        OrderStatus next;
        try {
            next = OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest("Invalid status: " + status);
        }
        OrderStatus previous = order.getStatus();
        order.setStatus(next);
        if (next == OrderStatus.Delivered && order.getDeliveredAt() == null) {
            order.setDeliveredAt(Instant.now());
        }
        // Cancelling an order returns any redeemed refund coupon to the customer.
        if (next == OrderStatus.Cancelled && previous != OrderStatus.Cancelled) {
            refundCouponService.restoreByCode(order.getCouponCode());
        }
        return orders.save(order);
    }

    /**
     * Reveals the plaintext OTP to the owning customer, enforcing the max-views cap.
     */
    @Transactional
    public String revealOtp(String orderId, Long userId) {
        Order order = get(orderId);
        if (!order.getUserId().equals(userId)) {
            throw ApiException.unauthorized("This order does not belong to you.");
        }
        if (order.isOtpUsed()) {
            throw ApiException.badRequest("OTP already used — order delivered.");
        }
        if (order.getOtpViews() >= otpService.maxViews()) {
            throw ApiException.badRequest("Maximum OTP views reached.");
        }
        order.setOtpViews(order.getOtpViews() + 1);
        orders.save(order);
        return order.getOtp();
    }

    /**
     * Delivery-agent validation: confirms the OTP and marks the order delivered.
     */
    @Transactional
    public Order validateDeliveryOtp(String orderId, String inputOtp) {
        Order order = get(orderId);
        if (order.isOtpUsed()) {
            throw ApiException.badRequest("OTP already used — order already delivered.");
        }
        if (!otpService.validate(inputOtp, orderId, order.getOtpHash())) {
            throw ApiException.badRequest("Invalid OTP. Do not deliver.");
        }
        order.setOtpUsed(true);
        order.setStatus(OrderStatus.Delivered);
        order.setDeliveredAt(Instant.now());
        return orders.save(order);
    }

    // ── helpers ─────────────────────────────────────────────────────────
    private ShippingAddress snapshot(Address a) {
        ShippingAddress s = new ShippingAddress();
        s.setLabel(a.getLabel());
        s.setName(a.getName());
        s.setPhone(a.getPhone());
        s.setLine1(a.getLine1());
        s.setCity(a.getCity());
        s.setState(a.getState());
        s.setPincode(a.getPincode());
        return s;
    }

    private int priceFor(Product product, int variantIndex) {
        List<ProductVariant> variants = product.getVariants();
        if (variantIndex >= 0 && variantIndex < variants.size()) {
            Integer vp = variants.get(variantIndex).getPrice();
            if (vp != null) {
                return vp;
            }
        }
        return product.getPrice() != null ? product.getPrice() : 0;
    }

    private String variantLabel(Product product, int variantIndex) {
        List<ProductVariant> variants = product.getVariants();
        if (variantIndex >= 0 && variantIndex < variants.size()) {
            return variants.get(variantIndex).getLabel();
        }
        return null;
    }

    private String normalizePayment(String payment) {
        String p = payment == null ? "" : payment.toLowerCase();
        if (!p.equals("upi") && !p.equals("cod")) {
            throw ApiException.badRequest("Payment must be 'upi' or 'cod'.");
        }
        return p;
    }

    /** "VNT" + last 8 digits of epoch millis + a 2-digit counter, matching the frontend shape. */
    private String generateOrderId() {
        long seq = SEQUENCE.incrementAndGet() % 100;
        return "VNT" + lastDigits(System.currentTimeMillis(), 8) + String.format("%02d", seq);
    }

    private String lastDigits(long value, int n) {
        String s = Long.toString(value);
        return s.length() <= n ? s : s.substring(s.length() - n);
    }
}
