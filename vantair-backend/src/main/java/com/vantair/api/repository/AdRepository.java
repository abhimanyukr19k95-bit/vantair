package com.vantair.api.repository;

import com.vantair.api.model.Ad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByActiveTrue();
    List<Ad> findBySlotAndActiveTrue(String slot);
    List<Ad> findAllByOrderByCreatedAtDesc();
}
