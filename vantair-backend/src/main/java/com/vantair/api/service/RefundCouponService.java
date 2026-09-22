package com.vantair.api.service;

import com.vantair.api.config.ApiException;
import com.vantair.api.dto.Dtos.IssueRefundRequest;
import com.vantair.api.model.RefundCoupon;
import com.vantair.api.model.User;
import com.vantair.api.repository.RefundCouponRepository;
import com.vantair.api.repository.UserRepository;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Store-credit coupons issued to a customer as a refund. Issued from the admin
 * Refunds panel and redeemable at checkout like any flat coupon (see OrderService).
 */
@Service
public class RefundCouponService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final RefundCouponRepository refunds;
    private final UserRepository users;

    public RefundCouponService(RefundCouponRepository refunds, UserRepository users) {
        this.refunds = refunds;
        this.users = users;
    }

    @Transactional
    public RefundCoupon issue(IssueRefundRequest req) {
        User user = users.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> ApiException.notFound(
                        "No customer found with email: " + req.email()));

        RefundCoupon coupon = new RefundCoupon();
        coupon.setCode(generateUniqueCode());
        coupon.setValue(req.amount());
        coupon.setReason(req.reason());
        coupon.setUser(user);
        return refunds.save(coupon);
    }

    @Transactional(readOnly = true)
    public List<RefundCoupon> listAll() {
        return refunds.findAllByOrderByIssuedAtDesc();
    }

    /** Returns the unused refund coupon matching this code for the given user, if any. */
    @Transactional(readOnly = true)
    public Optional<RefundCoupon> findRedeemable(String code, Long userId) {
        if (code == null) {
            return Optional.empty();
        }
        return refunds.findByCodeIgnoreCase(code.trim())
                .filter(c -> !c.isUsed())
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(userId));
    }

    @Transactional
    public void markUsed(Long refundCouponId) {
        refunds.findById(refundCouponId).ifPresent(c -> {
            c.setUsed(true);
            refunds.save(c);
        });
    }

    /**
     * Returns a redeemed refund coupon to the customer (e.g. when their order is
     * cancelled). No-op if the code isn't a known, used refund coupon.
     */
    @Transactional
    public void restoreByCode(String code) {
        if (code == null) {
            return;
        }
        refunds.findByCodeIgnoreCase(code.trim())
                .filter(RefundCoupon::isUsed)
                .ifPresent(c -> {
                    c.setUsed(false);
                    refunds.save(c);
                });
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            StringBuilder sb = new StringBuilder("REF");
            for (int i = 0; i < 6; i++) {
                sb.append(CODE_ALPHABET.charAt(RANDOM.nextInt(CODE_ALPHABET.length())));
            }
            String code = sb.toString();
            if (!refunds.existsByCodeIgnoreCase(code)) {
                return code;
            }
        }
        throw ApiException.conflict("Could not generate a unique refund code. Please retry.");
    }
}
