package com.vantair.api.repository;

import com.vantair.api.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByActiveTrue();
    List<Product> findByCategoryAndActiveTrue(String category);
}
