package com.vantair.api.repository;

import com.vantair.api.model.RefundCoupon;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundCouponRepository extends JpaRepository<RefundCoupon, Long> {
    Optional<RefundCoupon> findByCodeIgnoreCase(String code);
    List<RefundCoupon> findByUser_IdOrderByIssuedAtDesc(Long userId);
    List<RefundCoupon> findAllByOrderByIssuedAtDesc();
    boolean existsByCodeIgnoreCase(String code);
}
