package com.vantair.api.repository;

import com.vantair.api.model.PartnerEnquiry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerEnquiryRepository extends JpaRepository<PartnerEnquiry, Long> {
    List<PartnerEnquiry> findAllByOrderByCreatedAtDesc();
}
