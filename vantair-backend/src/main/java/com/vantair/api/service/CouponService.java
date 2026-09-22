package com.vantair.api.service;

import com.vantair.api.dto.Dtos.CouponResult;
import com.vantair.api.model.Coupon;
import com.vantair.api.repository.CouponRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {

    private final CouponRepository coupons;

    public CouponService(CouponRepository coupons) {
        this.coupons = coupons;
    }

    @Transactional(readOnly = true)
    public List<Coupon> listActive() {
        return coupons.findByActiveTrue();
    }

    /** Validates a coupon against a subtotal (in INR) and returns the computed discount. */
    @Transactional(readOnly = true)
    public CouponResult apply(String code, int subtotal) {
        Optional<Coupon> maybe = coupons.findByCodeIgnoreCaseAndActiveTrue(code.trim());
        if (maybe.isEmpty()) {
            return new CouponResult(false, 0, "Invalid coupon code", null, null);
        }
        Coupon c = maybe.get();
        if (c.getMinOrder() != null && subtotal < c.getMinOrder()) {
            return new CouponResult(false, 0,
                    "Minimum order of ₹" + c.getMinOrder() + " required", c.getCode(), c.getType());
        }
        int discount = computeDiscount(c, subtotal);
        return new CouponResult(true, discount, c.getDescription(), c.getCode(), c.getType());
    }

    /** Discount amount in INR. "shipping" coupons produce no line discount here. */
    public int computeDiscount(Coupon c, int subtotal) {
        return switch (c.getType()) {
            case "percent" -> Math.round(subtotal * (c.getValue() / 100f));
            case "flat" -> Math.min(c.getValue(), subtotal);
            default -> 0; // "shipping" handled as free delivery during checkout
        };
    }

    @Transactional(readOnly = true)
    public Optional<Coupon> findActive(String code) {
        return code == null ? Optional.empty() : coupons.findByCodeIgnoreCaseAndActiveTrue(code.trim());
    }
}
