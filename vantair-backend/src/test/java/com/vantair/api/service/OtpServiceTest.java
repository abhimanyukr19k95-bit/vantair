package com.vantair.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OtpServiceTest {

    private final OtpService service = new OtpService();

    @Test
    void generateOtp_isSixDigits() {
        for (int i = 0; i < 200; i++) {
            String otp = service.generateOtp();
            assertThat(otp).hasSize(6).containsOnlyDigits();
            assertThat(Integer.parseInt(otp)).isBetween(100000, 999999);
        }
    }

    @Test
    void hash_isDeterministicPerOrder() {
        assertThat(service.hash("123456", "VNT1")).isEqualTo(service.hash("123456", "VNT1"));
    }

    @Test
    void hash_differsByOrderId() {
        assertThat(service.hash("123456", "VNT1")).isNotEqualTo(service.hash("123456", "VNT2"));
    }

    @Test
    void validate_matchesGeneratedHash() {
        String hash = service.hash("654321", "VNT9");
        assertThat(service.validate("654321", "VNT9", hash)).isTrue();
        assertThat(service.validate("000000", "VNT9", hash)).isFalse();
    }

    @Test
    void maxViews_isThree() {
        assertThat(service.maxViews()).isEqualTo(3);
    }
}
