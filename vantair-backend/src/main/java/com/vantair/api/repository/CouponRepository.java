package com.vantair.api.repository;

import com.vantair.api.model.Coupon;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, String> {
    List<Coupon> findByActiveTrue();
    Optional<Coupon> findByCodeIgnoreCaseAndActiveTrue(String code);
}
