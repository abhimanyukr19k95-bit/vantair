package com.vantair.api.controller;

import com.vantair.api.dto.Dtos.CouponApplyRequest;
import com.vantair.api.dto.Dtos.CouponResult;
import com.vantair.api.model.Coupon;
import com.vantair.api.service.CouponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
@Tag(name = "Coupons", description = "List and validate discount coupons")
public class CouponController {

    private final CouponService coupons;

    public CouponController(CouponService coupons) {
        this.coupons = coupons;
    }

    @GetMapping
    public List<Coupon> list() {
        return coupons.listActive();
    }

    @PostMapping("/apply")
    public CouponResult apply(@Valid @RequestBody CouponApplyRequest req) {
        return coupons.apply(req.code(), req.subtotal());
    }
}
