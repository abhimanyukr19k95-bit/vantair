package com.vantair.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A single page view recorded by the storefront tracking beacon (js/data.js).
 * Aggregated by AnalyticsService to power the admin Traffic dashboard.
 */
@Entity
@Table(name = "page_views", indexes = {
        @Index(name = "idx_pageview_at", columnList = "viewedAt"),
        @Index(name = "idx_pageview_session", columnList = "sessionId")
})
@Getter
@Setter
@NoArgsConstructor
public class PageView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;

    @Column(length = 300)
    private String title;

    /** Anonymous per-browser session id (not a security session). */
    private String sessionId;

    /** Signed-in user id at view time, or null for anonymous visitors. */
    private Long userId;

    /** "mobile" | "tablet" | "desktop". */
    private String device;

    /** Referrer host, normalised (e.g. "google.com" or "direct"). */
    private String referrer;

    private Instant viewedAt = Instant.now();
}
