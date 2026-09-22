package com.vantair.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.vantair.api.TestFixtures;
import com.vantair.api.dto.Dtos.CouponResult;
import com.vantair.api.model.Coupon;
import com.vantair.api.repository.CouponRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock CouponRepository coupons;
    @InjectMocks CouponService service;

    @Test
    void listActive_delegates() {
        Coupon c = TestFixtures.coupon("VANTAIR10", "percent", 10, 500);
        when(coupons.findByActiveTrue()).thenReturn(List.of(c));
        assertThat(service.listActive()).containsExactly(c);
    }

    @Test
    void apply_invalidCode_returnsInvalid() {
        when(coupons.findByCodeIgnoreCaseAndActiveTrue("NOPE")).thenReturn(Optional.empty());

        CouponResult r = service.apply("NOPE", 1000);

        assertThat(r.valid()).isFalse();
        assertThat(r.discount()).isZero();
        assertThat(r.message()).isEqualTo("Invalid coupon code");
    }

    @Test
    void apply_belowMinOrder_returnsInvalidWithMessage() {
        when(coupons.findByCodeIgnoreCaseAndActiveTrue("FLAT200"))
                .thenReturn(Optional.of(TestFixtures.coupon("FLAT200", "flat", 200, 1499)));

        CouponResult r = service.apply("FLAT200", 1000);

        assertThat(r.valid()).isFalse();
        assertThat(r.message()).contains("Minimum order");
        assertThat(r.code()).isEqualTo("FLAT200");
    }

    @Test
    void apply_percent_computesRoundedDiscount() {
        when(coupons.findByCodeIgnoreCaseAndActiveTrue("VANTAIR10"))
                .thenReturn(Optional.of(TestFixtures.coupon("VANTAIR10", "percent", 10, 500)));

        CouponResult r = service.apply("VANTAIR10", 1005);

        assertThat(r.valid()).isTrue();
        assertThat(r.discount()).isEqualTo(Math.round(1005 * 0.10f)); // 101
        assertThat(r.type()).isEqualTo("percent");
    }

    @Test
    void apply_flat_capsAtSubtotal() {
        when(coupons.findByCodeIgnoreCaseAndActiveTrue("FLAT200"))
                .thenReturn(Optional.of(TestFixtures.coupon("FLAT200", "flat", 200, 0)));

        assertThat(service.apply("FLAT200", 150).discount()).isEqualTo(150);
        assertThat(service.apply("FLAT200", 500).discount()).isEqualTo(200);
    }

    @Test
    void apply_nullMinOrder_isValid() {
        Coupon c = TestFixtures.coupon("FREESHIP", "shipping", 0, null);
        when(coupons.findByCodeIgnoreCaseAndActiveTrue("FREESHIP")).thenReturn(Optional.of(c));

        CouponResult r = service.apply("FREESHIP", 10);

        assertThat(r.valid()).isTrue();
        assertThat(r.discount()).isZero(); // shipping → no line discount
    }

    @Test
    void computeDiscount_shippingType_isZero() {
        Coupon c = TestFixtures.coupon("FREESHIP", "shipping", 0, 0);
        assertThat(service.computeDiscount(c, 1000)).isZero();
    }

    @Test
    void findActive_nullCode_empty() {
        assertThat(service.findActive(null)).isEmpty();
    }

    @Test
    void findActive_trimsAndDelegates() {
        Coupon c = TestFixtures.coupon("VANTAIR10", "percent", 10, 500);
        when(coupons.findByCodeIgnoreCaseAndActiveTrue("VANTAIR10")).thenReturn(Optional.of(c));
        assertThat(service.findActive("  VANTAIR10 ")).contains(c);
    }
}
