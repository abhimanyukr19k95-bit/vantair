package com.vantair.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vantair.api.TestFixtures;
import com.vantair.api.config.ApiException;
import com.vantair.api.dto.Dtos.CartItemRequest;
import com.vantair.api.dto.Dtos.PlaceOrderRequest;
import com.vantair.api.model.Coupon;
import com.vantair.api.model.Order;
import com.vantair.api.model.OrderStatus;
import com.vantair.api.model.Product;
import com.vantair.api.model.RefundCoupon;
import com.vantair.api.model.User;
import com.vantair.api.repository.OrderRepository;
import com.vantair.api.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orders;
    @Mock ProductRepository products;
    @Mock UserService userService;
    @Mock CouponService couponService;
    @Mock RefundCouponService refundCouponService;
    @Mock DeliveryService deliveryService;
    @Mock OtpService otpService;
    @InjectMocks OrderService service;

    private User userWithAddress() {
        User u = TestFixtures.user(1L, "a@b.com");
        u.addAddress(TestFixtures.address(10L, "700001"));
        return u;
    }

    @BeforeEach
    void commonStubs() {
        lenient().when(orders.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        lenient().when(otpService.generateOtp()).thenReturn("123456");
        lenient().when(otpService.hash(anyString(), anyString())).thenReturn("HASH");
        lenient().when(deliveryService.expectedDelivery(any(), anyBoolean(), any())).thenReturn("Monday, 1 June");
    }

    private PlaceOrderRequest req(String payment, String coupon, String deliveryType, CartItemRequest... items) {
        return new PlaceOrderRequest(1L, 10L, payment, coupon, deliveryType, List.of(items));
    }

    private void stubProduct(Product p) {
        when(products.findById(p.getId())).thenReturn(Optional.of(p));
    }

    // ── placeOrder happy paths ────────────────────────────────
    @Test
    void placeOrder_standard_freeShippingAboveThreshold_noCoupon() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        Product p = TestFixtures.product("P001", "perfumes", 1000, TestFixtures.variant("50ml", 1000));
        stubProduct(p);
        when(couponService.findActive(null)).thenReturn(Optional.empty());
        when(refundCouponService.findRedeemable(null, 1L)).thenReturn(Optional.empty());
        when(deliveryService.deliveryCharge(1000, false)).thenReturn(0);

        Order o = service.placeOrder(req("upi", null, "standard", new CartItemRequest("P001", 0, 1)));

        assertThat(o.getSubtotal()).isEqualTo(1000);
        assertThat(o.getDiscount()).isZero();
        assertThat(o.getDelivery()).isZero();
        assertThat(o.getCodFee()).isZero();
        assertThat(o.getTotal()).isEqualTo(1000);
        assertThat(o.getStatus()).isEqualTo(OrderStatus.Confirmed);
        assertThat(o.getOtp()).isEqualTo("123456");
        assertThat(o.getId()).startsWith("VNT");
        verify(refundCouponService, never()).markUsed(any());
    }

    @Test
    void placeOrder_cod_addsCodFee_andStandardChargeBelowThreshold() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        Product p = TestFixtures.product("P001", "perfumes", 500, TestFixtures.variant("x", 500));
        stubProduct(p);
        when(couponService.findActive(null)).thenReturn(Optional.empty());
        when(refundCouponService.findRedeemable(null, 1L)).thenReturn(Optional.empty());
        when(deliveryService.deliveryCharge(500, false)).thenReturn(49);

        Order o = service.placeOrder(req("cod", null, "standard", new CartItemRequest("P001", 0, 1)));

        assertThat(o.getPayment()).isEqualTo("cod");
        assertThat(o.getCodFee()).isEqualTo(DeliveryService.COD_CHARGE);
        assertThat(o.getDelivery()).isEqualTo(49);
        assertThat(o.getTotal()).isEqualTo(500 + 49 + DeliveryService.COD_CHARGE);
    }

    @Test
    void placeOrder_percentCoupon_appliesDiscount() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        Product p = TestFixtures.product("P001", "perfumes", 1000, TestFixtures.variant("x", 1000));
        stubProduct(p);
        Coupon c = TestFixtures.coupon("VANTAIR10", "percent", 10, 500);
        when(couponService.findActive("VANTAIR10")).thenReturn(Optional.of(c));
        when(couponService.computeDiscount(c, 1000)).thenReturn(100);
        when(deliveryService.deliveryCharge(1000, false)).thenReturn(0);

        Order o = service.placeOrder(req("upi", "VANTAIR10", "standard", new CartItemRequest("P001", 0, 1)));

        assertThat(o.getDiscount()).isEqualTo(100);
        assertThat(o.getCouponCode()).isEqualTo("VANTAIR10");
        assertThat(o.getTotal()).isEqualTo(900);
    }

    @Test
    void placeOrder_shippingCoupon_makesDeliveryFree() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        Product p = TestFixtures.product("P001", "perfumes", 300, TestFixtures.variant("x", 300));
        stubProduct(p);
        Coupon c = TestFixtures.coupon("FREESHIP", "shipping", 0, 0);
        when(couponService.findActive("FREESHIP")).thenReturn(Optional.of(c));

        Order o = service.placeOrder(req("upi", "FREESHIP", "standard", new CartItemRequest("P001", 0, 1)));

        assertThat(o.getDelivery()).isZero();
        assertThat(o.getCouponCode()).isEqualTo("FREESHIP");
        verify(deliveryService, never()).deliveryCharge(anyInt(), anyBoolean());
    }

    @Test
    void placeOrder_couponBelowMinOrder_isIgnored() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        Product p = TestFixtures.product("P001", "perfumes", 100, TestFixtures.variant("x", 100));
        stubProduct(p);
        Coupon c = TestFixtures.coupon("FLAT200", "flat", 200, 1499);
        when(couponService.findActive("FLAT200")).thenReturn(Optional.of(c));
        when(deliveryService.deliveryCharge(100, false)).thenReturn(49);

        Order o = service.placeOrder(req("upi", "FLAT200", "standard", new CartItemRequest("P001", 0, 1)));

        assertThat(o.getDiscount()).isZero();
        assertThat(o.getCouponCode()).isNull();
    }

    @Test
    void placeOrder_refundCoupon_appliedAndBurned() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        Product p = TestFixtures.product("P001", "perfumes", 1500, TestFixtures.variant("x", 1500));
        stubProduct(p);
        when(couponService.findActive("REFXYZ")).thenReturn(Optional.empty());
        RefundCoupon rc = TestFixtures.refundCoupon(7L, "REFXYZ", 5000, TestFixtures.user(1L, "a@b.com"));
        when(refundCouponService.findRedeemable("REFXYZ", 1L)).thenReturn(Optional.of(rc));
        when(deliveryService.deliveryCharge(1500, false)).thenReturn(0);

        Order o = service.placeOrder(req("upi", "REFXYZ", "standard", new CartItemRequest("P001", 0, 1)));

        // discount capped at subtotal → order goes to 0
        assertThat(o.getDiscount()).isEqualTo(1500);
        assertThat(o.getTotal()).isZero();
        assertThat(o.getCouponCode()).isEqualTo("REFXYZ");
        // paid fully by coupon → payment mode recorded as "coupon", not "upi"
        assertThat(o.getPayment()).isEqualTo("coupon");
        verify(refundCouponService).markUsed(7L);
    }

    @Test
    void placeOrder_couponCoversFullStandardCoupon_paymentBecomesCoupon() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        Product p = TestFixtures.product("P001", "perfumes", 1000, TestFixtures.variant("x", 1000));
        stubProduct(p);
        Coupon c = TestFixtures.coupon("FLAT1000", "flat", 1000, 0);
        when(couponService.findActive("FLAT1000")).thenReturn(Optional.of(c));
        when(couponService.computeDiscount(c, 1000)).thenReturn(1000);
        when(deliveryService.deliveryCharge(1000, false)).thenReturn(0);

        Order o = service.placeOrder(req("upi", "FLAT1000", "standard", new CartItemRequest("P001", 0, 1)));
        assertThat(o.getTotal()).isZero();
        assertThat(o.getPayment()).isEqualTo("coupon");
    }

    @Test
    void placeOrder_express_usesExpressCharge_andNextDay() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        Product p = TestFixtures.product("P001", "perfumes", 800, TestFixtures.variant("x", 800));
        stubProduct(p);
        when(couponService.findActive(null)).thenReturn(Optional.empty());
        when(refundCouponService.findRedeemable(null, 1L)).thenReturn(Optional.empty());
        when(deliveryService.deliveryCharge(800, true)).thenReturn(149);

        Order o = service.placeOrder(req("upi", null, "express2", new CartItemRequest("P001", 0, 1)));

        assertThat(o.getDelivery()).isEqualTo(149);
        verify(deliveryService).expectedDelivery("700001", true, "Next Day");
    }

    @Test
    void placeOrder_variantPriceFallsBackToProductPrice_whenVariantPriceNull() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        Product p = TestFixtures.product("P001", "perfumes", 700, TestFixtures.variant("noprice", null));
        stubProduct(p);
        when(couponService.findActive(null)).thenReturn(Optional.empty());
        when(refundCouponService.findRedeemable(null, 1L)).thenReturn(Optional.empty());
        when(deliveryService.deliveryCharge(1400, false)).thenReturn(0);

        Order o = service.placeOrder(req("upi", null, "standard", new CartItemRequest("P001", 0, 2)));
        assertThat(o.getSubtotal()).isEqualTo(1400); // 700 * 2
        assertThat(o.getItems().get(0).getVariantLabel()).isEqualTo("noprice");
    }

    @Test
    void placeOrder_variantIndexOutOfRange_usesProductPriceAndNullLabel() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        Product p = TestFixtures.product("P001", "perfumes", 600); // no variants
        stubProduct(p);
        when(couponService.findActive(null)).thenReturn(Optional.empty());
        when(refundCouponService.findRedeemable(null, 1L)).thenReturn(Optional.empty());
        when(deliveryService.deliveryCharge(600, false)).thenReturn(49);

        Order o = service.placeOrder(req("upi", null, "standard", new CartItemRequest("P001", 5, 1)));
        assertThat(o.getItems().get(0).getPrice()).isEqualTo(600);
        assertThat(o.getItems().get(0).getVariantLabel()).isNull();
    }

    // ── placeOrder validation ─────────────────────────────────
    @Test
    void placeOrder_addressNotOwned_badRequest() {
        User u = TestFixtures.user(1L, "a@b.com"); // no addresses
        when(userService.get(1L)).thenReturn(u);

        assertThatThrownBy(() -> service.placeOrder(req("upi", null, "standard", new CartItemRequest("P001", 0, 1))))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void placeOrder_emptyItems_badRequest() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        assertThatThrownBy(() -> service.placeOrder(req("upi", null, "standard")))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void placeOrder_nullItems_badRequest() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        PlaceOrderRequest r = new PlaceOrderRequest(1L, 10L, "upi", null, "standard", null);
        assertThatThrownBy(() -> service.placeOrder(r)).isInstanceOf(ApiException.class);
    }

    @Test
    void placeOrder_unknownProduct_badRequest() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        when(products.findById("ZZZ")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.placeOrder(req("upi", null, "standard", new CartItemRequest("ZZZ", 0, 1))))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void placeOrder_inactiveProduct_badRequest() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        Product p = TestFixtures.product("P001", "perfumes", 500, TestFixtures.variant("x", 500));
        p.setActive(false);
        stubProduct(p);
        assertThatThrownBy(() -> service.placeOrder(req("upi", null, "standard", new CartItemRequest("P001", 0, 1))))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void placeOrder_invalidPayment_badRequest() {
        // Payment is normalised before the cart loop, so no product/coupon stubs are needed.
        when(userService.get(1L)).thenReturn(userWithAddress());
        assertThatThrownBy(() -> service.placeOrder(req("paypal", null, "standard", new CartItemRequest("P001", 0, 1))))
                .isInstanceOf(ApiException.class);
    }

    @Test
    void placeOrder_nullPayment_badRequest() {
        when(userService.get(1L)).thenReturn(userWithAddress());
        PlaceOrderRequest r = new PlaceOrderRequest(1L, 10L, null, null, "standard", List.of(new CartItemRequest("P001", 0, 1)));
        assertThatThrownBy(() -> service.placeOrder(r)).isInstanceOf(ApiException.class);
    }

    // ── get / lists ───────────────────────────────────────────
    @Test
    void get_found_andMissing() {
        Order o = TestFixtures.order("VNT1", 1L, 100, OrderStatus.Confirmed);
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));
        assertThat(service.get("VNT1")).isSameAs(o);

        when(orders.findById("X")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get("X")).isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listForUser_andListAll_delegate() {
        when(orders.findByUserIdOrderByPlacedAtDesc(1L)).thenReturn(List.of());
        when(orders.findAllByOrderByPlacedAtDesc()).thenReturn(List.of());
        assertThat(service.listForUser(1L)).isEmpty();
        assertThat(service.listAll()).isEmpty();
    }

    // ── updateStatus ──────────────────────────────────────────
    @Test
    void updateStatus_valid_setsStatus() {
        Order o = TestFixtures.order("VNT1", 1L, 100, OrderStatus.Confirmed);
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));

        Order out = service.updateStatus("VNT1", "Shipped");
        assertThat(out.getStatus()).isEqualTo(OrderStatus.Shipped);
    }

    @Test
    void updateStatus_delivered_setsDeliveredAt() {
        Order o = TestFixtures.order("VNT1", 1L, 100, OrderStatus.Shipped);
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));

        Order out = service.updateStatus("VNT1", "Delivered");
        assertThat(out.getDeliveredAt()).isNotNull();
    }

    @Test
    void updateStatus_invalid_badRequest() {
        Order o = TestFixtures.order("VNT1", 1L, 100, OrderStatus.Confirmed);
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));
        assertThatThrownBy(() -> service.updateStatus("VNT1", "Nonsense"))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateStatus_cancel_restoresRefundCoupon() {
        Order o = TestFixtures.order("VNT1", 1L, 0, OrderStatus.Confirmed);
        o.setCouponCode("REFXYZ");
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));

        service.updateStatus("VNT1", "Cancelled");
        verify(refundCouponService).restoreByCode("REFXYZ");
    }

    @Test
    void updateStatus_cancelAlreadyCancelled_doesNotRestoreAgain() {
        Order o = TestFixtures.order("VNT1", 1L, 0, OrderStatus.Cancelled);
        o.setCouponCode("REFXYZ");
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));

        service.updateStatus("VNT1", "Cancelled");
        verify(refundCouponService, never()).restoreByCode(any());
    }

    @Test
    void updateStatus_nonCancel_doesNotRestore() {
        Order o = TestFixtures.order("VNT1", 1L, 100, OrderStatus.Confirmed);
        o.setCouponCode("REFXYZ");
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));

        service.updateStatus("VNT1", "Shipped");
        verify(refundCouponService, never()).restoreByCode(any());
    }

    // ── revealOtp ─────────────────────────────────────────────
    @Test
    void revealOtp_ok_incrementsViews() {
        Order o = TestFixtures.order("VNT1", 1L, 100, OrderStatus.Confirmed);
        o.setOtp("999000");
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));
        when(otpService.maxViews()).thenReturn(3);

        assertThat(service.revealOtp("VNT1", 1L)).isEqualTo("999000");
        assertThat(o.getOtpViews()).isEqualTo(1);
    }

    @Test
    void revealOtp_notOwner_unauthorized() {
        Order o = TestFixtures.order("VNT1", 1L, 100, OrderStatus.Confirmed);
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));
        assertThatThrownBy(() -> service.revealOtp("VNT1", 2L))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void revealOtp_alreadyUsed_badRequest() {
        Order o = TestFixtures.order("VNT1", 1L, 100, OrderStatus.Delivered);
        o.setOtpUsed(true);
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));
        assertThatThrownBy(() -> service.revealOtp("VNT1", 1L)).isInstanceOf(ApiException.class);
    }

    @Test
    void revealOtp_maxViewsReached_badRequest() {
        Order o = TestFixtures.order("VNT1", 1L, 100, OrderStatus.Confirmed);
        o.setOtpViews(3);
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));
        when(otpService.maxViews()).thenReturn(3);
        assertThatThrownBy(() -> service.revealOtp("VNT1", 1L)).isInstanceOf(ApiException.class);
    }

    // ── validateDeliveryOtp ───────────────────────────────────
    @Test
    void validateDeliveryOtp_ok_marksDelivered() {
        Order o = TestFixtures.order("VNT1", 1L, 100, OrderStatus.OutForDelivery);
        o.setOtpHash("HASH");
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));
        when(otpService.validate("123456", "VNT1", "HASH")).thenReturn(true);

        Order out = service.validateDeliveryOtp("VNT1", "123456");
        assertThat(out.isOtpUsed()).isTrue();
        assertThat(out.getStatus()).isEqualTo(OrderStatus.Delivered);
        assertThat(out.getDeliveredAt()).isNotNull();
    }

    @Test
    void validateDeliveryOtp_alreadyUsed_badRequest() {
        Order o = TestFixtures.order("VNT1", 1L, 100, OrderStatus.Delivered);
        o.setOtpUsed(true);
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));
        assertThatThrownBy(() -> service.validateDeliveryOtp("VNT1", "123456")).isInstanceOf(ApiException.class);
    }

    @Test
    void validateDeliveryOtp_wrongOtp_badRequest() {
        Order o = TestFixtures.order("VNT1", 1L, 100, OrderStatus.OutForDelivery);
        o.setOtpHash("HASH");
        when(orders.findById("VNT1")).thenReturn(Optional.of(o));
        when(otpService.validate(eq("000000"), eq("VNT1"), eq("HASH"))).thenReturn(false);
        assertThatThrownBy(() -> service.validateDeliveryOtp("VNT1", "000000")).isInstanceOf(ApiException.class);
    }
}
