package com.vantair.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vantair.api.TestFixtures;
import com.vantair.api.config.ApiException;
import com.vantair.api.dto.Dtos.IssueRefundRequest;
import com.vantair.api.model.RefundCoupon;
import com.vantair.api.model.User;
import com.vantair.api.repository.RefundCouponRepository;
import com.vantair.api.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class RefundCouponServiceTest {

    @Mock RefundCouponRepository refunds;
    @Mock UserRepository users;
    @InjectMocks RefundCouponService service;

    @Test
    void issue_createsUniqueCodeForCustomer() {
        User u = TestFixtures.user(1L, "a@b.com");
        when(users.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(u));
        when(refunds.existsByCodeIgnoreCase(any())).thenReturn(false);
        when(refunds.save(any(RefundCoupon.class))).thenAnswer(i -> i.getArgument(0));

        RefundCoupon out = service.issue(new IssueRefundRequest("a@b.com", 500, "Damaged"));

        assertThat(out.getCode()).startsWith("REF").hasSize(9);
        assertThat(out.getValue()).isEqualTo(500);
        assertThat(out.getReason()).isEqualTo("Damaged");
        assertThat(out.getUser()).isSameAs(u);
    }

    @Test
    void issue_retriesUntilUniqueCode() {
        User u = TestFixtures.user(1L, "a@b.com");
        when(users.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(u));
        // First generated code collides, second is free.
        when(refunds.existsByCodeIgnoreCase(any())).thenReturn(true, false);
        when(refunds.save(any(RefundCoupon.class))).thenAnswer(i -> i.getArgument(0));

        RefundCoupon out = service.issue(new IssueRefundRequest("a@b.com", 100, null));
        assertThat(out.getCode()).startsWith("REF");
        verify(refunds, never()).save(null);
    }

    @Test
    void issue_unknownEmail_notFound() {
        when(users.findByEmailIgnoreCase("none@b.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.issue(new IssueRefundRequest("none@b.com", 100, null)))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listAll_delegates() {
        when(refunds.findAllByOrderByIssuedAtDesc()).thenReturn(List.of());
        assertThat(service.listAll()).isEmpty();
    }

    // ── findRedeemable ────────────────────────────────────────
    @Test
    void findRedeemable_nullCode_empty() {
        assertThat(service.findRedeemable(null, 1L)).isEmpty();
    }

    @Test
    void findRedeemable_unusedAndOwned_returnsIt() {
        User u = TestFixtures.user(1L, "a@b.com");
        RefundCoupon rc = TestFixtures.refundCoupon(5L, "REFAAAAAA", 200, u);
        when(refunds.findByCodeIgnoreCase("REFAAAAAA")).thenReturn(Optional.of(rc));

        assertThat(service.findRedeemable(" REFAAAAAA ", 1L)).contains(rc);
    }

    @Test
    void findRedeemable_used_empty() {
        User u = TestFixtures.user(1L, "a@b.com");
        RefundCoupon rc = TestFixtures.refundCoupon(5L, "REFAAAAAA", 200, u);
        rc.setUsed(true);
        when(refunds.findByCodeIgnoreCase("REFAAAAAA")).thenReturn(Optional.of(rc));

        assertThat(service.findRedeemable("REFAAAAAA", 1L)).isEmpty();
    }

    @Test
    void findRedeemable_otherUser_empty() {
        User u = TestFixtures.user(2L, "other@b.com");
        RefundCoupon rc = TestFixtures.refundCoupon(5L, "REFAAAAAA", 200, u);
        when(refunds.findByCodeIgnoreCase("REFAAAAAA")).thenReturn(Optional.of(rc));

        assertThat(service.findRedeemable("REFAAAAAA", 1L)).isEmpty();
    }

    @Test
    void findRedeemable_nullOwner_empty() {
        RefundCoupon rc = TestFixtures.refundCoupon(5L, "REFAAAAAA", 200, null);
        when(refunds.findByCodeIgnoreCase("REFAAAAAA")).thenReturn(Optional.of(rc));

        assertThat(service.findRedeemable("REFAAAAAA", 1L)).isEmpty();
    }

    // ── markUsed ──────────────────────────────────────────────
    @Test
    void markUsed_setsFlagAndSaves() {
        RefundCoupon rc = TestFixtures.refundCoupon(5L, "REFAAAAAA", 200, TestFixtures.user(1L, "a@b.com"));
        when(refunds.findById(5L)).thenReturn(Optional.of(rc));

        service.markUsed(5L);

        ArgumentCaptor<RefundCoupon> captor = ArgumentCaptor.forClass(RefundCoupon.class);
        verify(refunds).save(captor.capture());
        assertThat(captor.getValue().isUsed()).isTrue();
    }

    @Test
    void markUsed_missing_noSave() {
        when(refunds.findById(9L)).thenReturn(Optional.empty());
        service.markUsed(9L);
        verify(refunds, never()).save(any());
    }

    // ── restoreByCode ─────────────────────────────────────────
    @Test
    void restoreByCode_usedCoupon_isUnmarked() {
        RefundCoupon rc = TestFixtures.refundCoupon(5L, "REFAAAAAA", 200, TestFixtures.user(1L, "a@b.com"));
        rc.setUsed(true);
        when(refunds.findByCodeIgnoreCase("REFAAAAAA")).thenReturn(Optional.of(rc));

        service.restoreByCode("  REFAAAAAA ");

        ArgumentCaptor<RefundCoupon> captor = ArgumentCaptor.forClass(RefundCoupon.class);
        verify(refunds).save(captor.capture());
        assertThat(captor.getValue().isUsed()).isFalse();
    }

    @Test
    void restoreByCode_alreadyUnused_noSave() {
        RefundCoupon rc = TestFixtures.refundCoupon(5L, "REFAAAAAA", 200, TestFixtures.user(1L, "a@b.com"));
        when(refunds.findByCodeIgnoreCase("REFAAAAAA")).thenReturn(Optional.of(rc));
        service.restoreByCode("REFAAAAAA");
        verify(refunds, never()).save(any());
    }

    @Test
    void restoreByCode_null_isNoOp() {
        service.restoreByCode(null);
        verify(refunds, never()).save(any());
    }

    @Test
    void restoreByCode_unknownCode_isNoOp() {
        when(refunds.findByCodeIgnoreCase("NOPE")).thenReturn(Optional.empty());
        service.restoreByCode("NOPE");
        verify(refunds, never()).save(any());
    }
}
