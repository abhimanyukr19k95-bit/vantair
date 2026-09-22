package com.vantair.api.service;

import com.vantair.api.config.ApiException;
import com.vantair.api.dto.Dtos.AdRequest;
import com.vantair.api.model.Ad;
import com.vantair.api.repository.AdRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CRUD for promotional banner ads (admin) and active-ad lookup (storefront). */
@Service
public class AdService {

    private final AdRepository ads;

    public AdService(AdRepository ads) {
        this.ads = ads;
    }

    /** Active ads, optionally filtered to a placement slot — for the storefront. */
    @Transactional(readOnly = true)
    public List<Ad> listActive(String slot) {
        if (slot != null && !slot.isBlank()) {
            return ads.findBySlotAndActiveTrue(slot);
        }
        return ads.findByActiveTrue();
    }

    /** Every ad, newest first — for the admin panel. */
    @Transactional(readOnly = true)
    public List<Ad> listAll() {
        return ads.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Ad create(AdRequest req) {
        Ad ad = new Ad();
        apply(ad, req);
        return ads.save(ad);
    }

    @Transactional
    public Ad update(Long id, AdRequest req) {
        Ad ad = get(id);
        apply(ad, req);
        return ads.save(ad);
    }

    @Transactional
    public Ad setActive(Long id, boolean active) {
        Ad ad = get(id);
        ad.setActive(active);
        return ads.save(ad);
    }

    @Transactional
    public void delete(Long id) {
        if (!ads.existsById(id)) {
            throw ApiException.notFound("Ad not found: " + id);
        }
        ads.deleteById(id);
    }

    private Ad get(Long id) {
        return ads.findById(id).orElseThrow(() -> ApiException.notFound("Ad not found: " + id));
    }

    private void apply(Ad ad, AdRequest req) {
        ad.setTitle(req.title());
        ad.setText(req.text());
        ad.setImage(req.image());
        ad.setLink(req.link());
        ad.setSlot(req.slot() == null || req.slot().isBlank() ? "home" : req.slot());
        if (req.active() != null) {
            ad.setActive(req.active());
        }
    }
}
