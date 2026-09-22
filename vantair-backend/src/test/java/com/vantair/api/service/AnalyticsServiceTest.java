package com.vantair.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vantair.api.dto.Dtos.TrackEventRequest;
import com.vantair.api.model.PageView;
import com.vantair.api.repository.PageViewRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock PageViewRepository pageViews;
    @InjectMocks AnalyticsService service;

    private PageView view(String path, String session, Long userId, String device, String referrer, Instant at) {
        PageView v = new PageView();
        v.setPath(path);
        v.setTitle("title");
        v.setSessionId(session);
        v.setUserId(userId);
        v.setDevice(device);
        v.setReferrer(referrer);
        v.setViewedAt(at);
        return v;
    }

    // ── track / normalisation ─────────────────────────────────
    @Test
    void track_normalisesDeviceReferrerAndPersists() {
        service.track(new TrackEventRequest("/shop.html", "Shop", "sessA", 1L, "Mobile",
                "https://www.google.com/search?q=x"));

        ArgumentCaptor<PageView> captor = ArgumentCaptor.forClass(PageView.class);
        verify(pageViews).save(captor.capture());
        PageView v = captor.getValue();
        assertThat(v.getDevice()).isEqualTo("mobile");
        assertThat(v.getReferrer()).isEqualTo("google.com"); // www. stripped
        assertThat(v.getPath()).isEqualTo("/shop.html");
    }

    @Test
    void track_unknownDevice_andDirectReferrer() {
        service.track(new TrackEventRequest("/", null, "s", null, "smartfridge", ""));
        ArgumentCaptor<PageView> captor = ArgumentCaptor.forClass(PageView.class);
        verify(pageViews).save(captor.capture());
        assertThat(captor.getValue().getDevice()).isEqualTo("unknown");
        assertThat(captor.getValue().getReferrer()).isEqualTo("direct");
    }

    @Test
    void track_nullDevice_andMalformedReferrer() {
        service.track(new TrackEventRequest("/x", null, "s", null, null, "ht!tp://[bad"));
        ArgumentCaptor<PageView> captor = ArgumentCaptor.forClass(PageView.class);
        verify(pageViews).save(captor.capture());
        assertThat(captor.getValue().getDevice()).isEqualTo("unknown");
        assertThat(captor.getValue().getReferrer()).isEqualTo("other");
    }

    @Test
    void track_referrerWithoutHost_isDirect() {
        service.track(new TrackEventRequest("/x", null, "s", null, "desktop", "mailto:foo@bar.com"));
        ArgumentCaptor<PageView> captor = ArgumentCaptor.forClass(PageView.class);
        verify(pageViews).save(captor.capture());
        assertThat(captor.getValue().getReferrer()).isEqualTo("direct");
    }

    @Test
    void track_longPathAndTitle_areTruncated() {
        String longStr = "x".repeat(400);
        service.track(new TrackEventRequest(longStr, longStr, "s", null, "desktop", "direct"));
        ArgumentCaptor<PageView> captor = ArgumentCaptor.forClass(PageView.class);
        verify(pageViews).save(captor.capture());
        assertThat(captor.getValue().getPath()).hasSize(300);
        assertThat(captor.getValue().getTitle()).hasSize(300);
    }

    // ── dashboard aggregation ─────────────────────────────────
    @Test
    void dashboard_aggregatesWindows() {
        // Anchor "today" events to now (avoids midnight-boundary flakiness) and
        // place the one non-today event 26h ago — still inside the 7-day window.
        Instant now = Instant.now();
        Instant yesterday = now.minus(26, ChronoUnit.HOURS);

        List<PageView> recent = new ArrayList<>(List.of(
                view("/shop.html", "s1", 1L, "mobile", "google.com", now),
                view("/shop.html", "s1", 1L, "mobile", "google.com", now),
                view("/cart.html", "s2", null, "desktop", "direct", now),
                view("/old.html", "s3", 2L, "tablet", "direct", yesterday)
        ));
        when(pageViews.findByViewedAtAfter(any())).thenReturn(recent);
        when(pageViews.count()).thenReturn(123L);

        Map<String, Object> d = service.dashboard();

        assertThat(d.get("totalViews")).isEqualTo(123L);
        // active sessions: s1 + s2 (both have an event in the last 5 min)
        assertThat(d.get("activeSessions")).isEqualTo(2L);
        // online customers: only user 1 is active (user 2's only view is yesterday)
        assertThat(d.get("onlineCustomers")).isEqualTo(1L);
        // today's views: 3 (excludes yesterday)
        assertThat(d.get("viewsToday")).isEqualTo(3);
        assertThat(d.get("uniqueVisitorsToday")).isEqualTo(2L); // s1, s2

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topPages = (List<Map<String, Object>>) d.get("topPages");
        assertThat(topPages.get(0).get("path")).isEqualTo("/shop.html"); // most viewed today
        assertThat(topPages.get(0).get("count")).isEqualTo(2L);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> daily = (List<Map<String, Object>>) d.get("dailyTraffic");
        assertThat(daily).hasSize(7); // gap-filled 7-day series

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> devices = (List<Map<String, Object>>) d.get("devices");
        assertThat(devices).isNotEmpty();
    }

    @Test
    void dashboard_emptyData_returnsZeros() {
        when(pageViews.findByViewedAtAfter(any())).thenReturn(List.of());
        when(pageViews.count()).thenReturn(0L);

        Map<String, Object> d = service.dashboard();

        assertThat(d.get("totalViews")).isEqualTo(0L);
        assertThat(d.get("activeSessions")).isEqualTo(0L);
        assertThat(d.get("onlineCustomers")).isEqualTo(0L);
        assertThat(d.get("viewsToday")).isEqualTo(0);
    }

    @Test
    void dashboard_blankAndNullFields_bucketedSafely() {
        Instant now = Instant.now();
        List<PageView> recent = List.of(
                view(null, "", null, null, null, now),   // blank session, null device/referrer/path
                view("/x", "  ", null, "  ", "  ", now)   // blank-ish values
        );
        when(pageViews.findByViewedAtAfter(any())).thenReturn(recent);
        when(pageViews.count()).thenReturn(2L);

        Map<String, Object> d = service.dashboard();

        // blank session ids are not counted as unique visitors
        assertThat(d.get("uniqueVisitorsToday")).isEqualTo(0L);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> devices = (List<Map<String, Object>>) d.get("devices");
        assertThat(devices.get(0).get("label")).isEqualTo("unknown");
    }
}
