package com.vantair.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "partner_enquiries")
@Getter
@Setter
@NoArgsConstructor
public class PartnerEnquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String company;
    private String email;
    private String phone;
    private String type;
    private String volume;

    @Column(length = 2000)
    private String message;

    private Instant createdAt = Instant.now();
}
