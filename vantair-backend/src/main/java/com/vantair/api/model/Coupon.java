package com.vantair.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
public class Coupon {

    /** The coupon code, e.g. "VANTAIR10". */
    @Id
    private String code;

    /** One of: percent, flat, shipping. */
    @Column(nullable = false)
    private String type;

    private Integer value;

    private Integer minOrder;

    @Column(length = 300)
    private String description;

    private boolean active = true;
}
