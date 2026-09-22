package com.vantair.api.repository;

import com.vantair.api.model.PageView;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageViewRepository extends JpaRepository<PageView, Long> {
    List<PageView> findByViewedAtAfter(Instant since);
    long countByViewedAtAfter(Instant since);
}
