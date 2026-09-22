package com.vantair.api.repository;

import com.vantair.api.model.Order;
import com.vantair.api.model.OrderStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserIdOrderByPlacedAtDesc(Long userId);
    List<Order> findAllByOrderByPlacedAtDesc();
    List<Order> findByStatusOrderByPlacedAtDesc(OrderStatus status);
}
