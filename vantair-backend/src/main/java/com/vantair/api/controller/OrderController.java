package com.vantair.api.controller;

import com.vantair.api.dto.Dtos.PlaceOrderRequest;
import com.vantair.api.dto.Dtos.UpdateOrderStatusRequest;
import com.vantair.api.model.Order;
import com.vantair.api.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Place orders, track status and reveal the delivery OTP")
public class OrderController {

    private final OrderService orders;

    public OrderController(OrderService orders) {
        this.orders = orders;
    }

    @PostMapping
    public ResponseEntity<Order> place(@Valid @RequestBody PlaceOrderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orders.placeOrder(req));
    }

    /** List orders, optionally filtered by user via ?userId=. */
    @GetMapping
    public List<Order> list(@RequestParam(required = false) Long userId) {
        return userId != null ? orders.listForUser(userId) : orders.listAll();
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable String id) {
        return orders.get(id);
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable String id, @Valid @RequestBody UpdateOrderStatusRequest req) {
        return orders.updateStatus(id, req.status());
    }

    /**
     * Reveals the delivery OTP to the owning customer (enforces the view cap).
     * The customer is identified by ?userId= since auth is not wired yet.
     */
    @PostMapping("/{id}/reveal-otp")
    public Map<String, String> revealOtp(@PathVariable String id, @RequestParam Long userId) {
        return Map.of("otp", orders.revealOtp(id, userId));
    }
}
