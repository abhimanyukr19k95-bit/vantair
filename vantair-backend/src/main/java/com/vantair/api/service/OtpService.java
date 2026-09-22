package com.vantair.api.service;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

/**
 * Delivery OTP generation and verification. Mirrors the frontend's scheme
 * (otp.js): a 6-digit OTP plus a salted hash the delivery agent validates
 * against, so existing frontend expectations still hold.
 */
@Component
public class OtpService {

    private static final String SALT = "VNT_SEC_2025";
    private static final int MAX_VIEWS = 3;
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateOtp() {
        return String.valueOf(100000 + RANDOM.nextInt(900000));
    }

    public String hash(String otp, String orderId) {
        return simpleHash(otp + SALT + orderId);
    }

    public boolean validate(String inputOtp, String orderId, String storedHash) {
        return hash(inputOtp, orderId).equals(storedHash);
    }

    public int maxViews() {
        return MAX_VIEWS;
    }

    /** Faithful port of the frontend's simpleHash (djb2-ish, base36 upper). */
    private String simpleHash(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            int ch = str.charAt(i);
            hash = ((hash << 5) - hash) + ch;
            // emulate JS "hash = hash & hash" 32-bit wrap (int already 32-bit in Java)
        }
        return Integer.toString(Math.abs(hash), 36).toUpperCase();
    }
}
