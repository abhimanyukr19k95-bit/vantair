package com.vantair.api.controller;

import com.vantair.api.dto.Dtos.ValidateOtpRequest;
import com.vantair.api.model.Order;
import com.vantair.api.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Delivery-agent endpoints (delivery-validate.html). Validating the OTP marks
 * the order delivered.
 */
@RestController
@RequestMapping("/api/delivery")
@Tag(name = "Delivery", description = "Delivery-agent OTP validation")
public class DeliveryController {

    private final OrderService orders;

    public DeliveryController(OrderService orders) {
        this.orders = orders;
    }

    @PostMapping("/{orderId}/validate-otp")
    public Order validate(@PathVariable String orderId, @Valid @RequestBody ValidateOtpRequest req) {
        return orders.validateDeliveryOtp(orderId, req.otp());
    }
}
