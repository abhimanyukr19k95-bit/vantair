package com.vantair.api.controller;

import com.vantair.api.model.Ad;
import com.vantair.api.service.AdService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Public endpoint the storefront uses to fetch active promotional ads. */
@RestController
@RequestMapping("/api/ads")
@Tag(name = "Ads", description = "Active promotional banners for the storefront")
public class AdController {

    private final AdService ads;

    public AdController(AdService ads) {
        this.ads = ads;
    }

    @GetMapping
    public List<Ad> active(@RequestParam(required = false) String slot) {
        return ads.listActive(slot);
    }
}
