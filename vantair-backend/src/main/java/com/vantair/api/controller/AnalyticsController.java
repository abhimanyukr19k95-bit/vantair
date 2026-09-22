package com.vantair.api.controller;

import com.vantair.api.dto.Dtos.TrackEventRequest;
import com.vantair.api.service.AnalyticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public endpoint the storefront beacon posts page views to. Fire-and-forget:
 * always returns 202 and never blocks the page.
 */
@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Storefront traffic tracking beacon")
public class AnalyticsController {

    private final AnalyticsService analytics;

    public AnalyticsController(AnalyticsService analytics) {
        this.analytics = analytics;
    }

    @PostMapping("/track")
    public ResponseEntity<Void> track(@Valid @RequestBody TrackEventRequest req) {
        analytics.track(req);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
