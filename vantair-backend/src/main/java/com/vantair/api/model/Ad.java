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

/**
 * A customer-facing promotional banner ad, managed from the admin Ads panel and
 * shown in named placement slots on the storefront (e.g. "home", "shop").
 */
@Entity
@Table(name = "ads")
@Getter
@Setter
@NoArgsConstructor
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 500)
    private String text;

    /** Emoji or short image url shown alongside the text. */
    private String image;

    /** Where the ad links to when clicked. */
    private String link;

    /** Placement slot, e.g. "home" or "shop". */
    private String slot;

    private boolean active = true;

    private Instant createdAt = Instant.now();
}
