package com.vantair.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class DeliveryServiceTest {

    private final DeliveryService service = new DeliveryService();

    @Test
    void isExpressAvailable() {
        assertThat(service.isExpressAvailable("700001")).isTrue();
        assertThat(service.isExpressAvailable("999999")).isFalse();
        assertThat(service.isExpressAvailable(null)).isFalse();
    }

    @Test
    void deliveryCharge_express_isFlatExpressCharge() {
        assertThat(service.deliveryCharge(100, true)).isEqualTo(DeliveryService.EXPRESS_CHARGE);
    }

    @Test
    void deliveryCharge_standard_freeAboveThreshold() {
        assertThat(service.deliveryCharge(999, false)).isZero();
        assertThat(service.deliveryCharge(998, false)).isEqualTo(DeliveryService.STANDARD_CHARGE);
    }

    @Test
    void expectedDelivery_sameDay_isToday() {
        String expected = format(LocalDate.now());
        assertThat(service.expectedDelivery("700001", true, "Same Day")).isEqualTo(expected);
    }

    @Test
    void expectedDelivery_nextDay_isTomorrow() {
        String expected = format(LocalDate.now().plusDays(1));
        assertThat(service.expectedDelivery("700001", true, "Next Day")).isEqualTo(expected);
    }

    @Test
    void expectedDelivery_standard_knownState_usesRegionDays() {
        // 700 → WB → 1 day
        assertThat(service.expectedDelivery("700001", false, null))
                .isEqualTo(format(LocalDate.now().plusDays(1)));
    }

    @Test
    void expectedDelivery_unknownPrefix_usesDefaultDays() {
        // unknown prefix → DEFAULT → 5 days
        assertThat(service.expectedDelivery("999123", false, null))
                .isEqualTo(format(LocalDate.now().plusDays(5)));
    }

    @Test
    void expectedDelivery_nullPincode_fallsBackTo700() {
        assertThat(service.expectedDelivery(null, false, null))
                .isEqualTo(format(LocalDate.now().plusDays(1)));
    }

    @Test
    void expectedDelivery_shortPincode_fallsBackTo700() {
        assertThat(service.expectedDelivery("70", false, null))
                .isEqualTo(format(LocalDate.now().plusDays(1)));
    }

    private String format(LocalDate date) {
        String weekday = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return weekday + ", " + date.getDayOfMonth() + " " + month;
    }
}
