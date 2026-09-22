package com.vantair.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vantair.api.config.ApiException;
import com.vantair.api.dto.Dtos.AdRequest;
import com.vantair.api.model.Ad;
import com.vantair.api.repository.AdRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {

    @Mock AdRepository ads;
    @InjectMocks AdService service;

    private AdRequest req(String slot, Boolean active) {
        return new AdRequest("Sale", "20% off", "🌧️", "shop.html", slot, active);
    }

    @Test
    void listActive_withSlot() {
        when(ads.findBySlotAndActiveTrue("home")).thenReturn(List.of(new Ad()));
        assertThat(service.listActive("home")).hasSize(1);
    }

    @Test
    void listActive_noSlot() {
        when(ads.findByActiveTrue()).thenReturn(List.of());
        assertThat(service.listActive(null)).isEmpty();
        assertThat(service.listActive("  ")).isEmpty();
    }

    @Test
    void listAll_delegates() {
        when(ads.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(new Ad()));
        assertThat(service.listAll()).hasSize(1);
    }

    @Test
    void create_mapsFields() {
        when(ads.save(org.mockito.ArgumentMatchers.any(Ad.class))).thenAnswer(i -> i.getArgument(0));
        Ad ad = service.create(req("shop", true));
        assertThat(ad.getTitle()).isEqualTo("Sale");
        assertThat(ad.getSlot()).isEqualTo("shop");
        assertThat(ad.isActive()).isTrue();
    }

    @Test
    void create_blankSlot_defaultsToHome_andNullActiveKeepsDefault() {
        when(ads.save(org.mockito.ArgumentMatchers.any(Ad.class))).thenAnswer(i -> i.getArgument(0));
        Ad ad = service.create(req(null, null));
        assertThat(ad.getSlot()).isEqualTo("home");
        assertThat(ad.isActive()).isTrue(); // entity default
    }

    @Test
    void update_existing_appliesChanges() {
        Ad existing = new Ad();
        existing.setId(3L);
        when(ads.findById(3L)).thenReturn(Optional.of(existing));
        when(ads.save(existing)).thenReturn(existing);

        Ad out = service.update(3L, req("home", false));
        assertThat(out.getTitle()).isEqualTo("Sale");
        assertThat(out.isActive()).isFalse();
    }

    @Test
    void update_missing_notFound() {
        when(ads.findById(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(9L, req("home", true)))
                .isInstanceOf(ApiException.class)
                .extracting(e -> ((ApiException) e).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void setActive_togglesAndSaves() {
        Ad ad = new Ad();
        ad.setId(1L);
        ad.setActive(true);
        when(ads.findById(1L)).thenReturn(Optional.of(ad));
        when(ads.save(ad)).thenReturn(ad);

        assertThat(service.setActive(1L, false).isActive()).isFalse();
    }

    @Test
    void delete_existing() {
        when(ads.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(ads).deleteById(1L);
    }

    @Test
    void delete_missing_notFound() {
        when(ads.existsById(9L)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(9L)).isInstanceOf(ApiException.class);
        verify(ads, never()).deleteById(9L);
    }
}
