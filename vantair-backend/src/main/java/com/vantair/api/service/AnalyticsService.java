package com.vantair.api.service;

import com.vantair.api.dto.Dtos.TrackEventRequest;
import com.vantair.api.model.PageView;
import com.vantair.api.repository.PageViewRepository;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Records storefront page views and aggregates them into the traffic metrics
 * shown on the admin dashboard. A visitor is considered "online" if they have
 * viewed a page within {@link #ACTIVE_WINDOW}.
 */
@Service
public class AnalyticsService {

    /** A session counts as "active"/"online" if seen within this window. */
    private static final Duration ACTIVE_WINDOW = Duration.ofMinutes(5);
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final int TOP_PAGES = 8;
    private static final int TRAFFIC_DAYS = 7;

    private final PageViewRepository pageViews;

    public AnalyticsService(PageViewRepository pageViews) {
        this.pageViews = pageViews;
    }

    @Transactional
    public void track(TrackEventRequest req) {
        PageView view = new PageView();
        view.setPath(trim(req.path(), 300));
        view.setTitle(trim(req.title(), 300));
        view.setSessionId(req.sessionId());
        view.setUserId(req.userId());
        view.setDevice(normaliseDevice(req.device()));
        view.setReferrer(normaliseReferrer(req.referrer()));
        pageViews.save(view);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> dashboard() {
        Instant now = Instant.now();
        Instant activeSince = now.minus(ACTIVE_WINDOW);
        Instant dayStart = LocalDate.now(IST).atStartOfDay(IST).toInstant();
        Instant windowStart = LocalDate.now(IST).minusDays(TRAFFIC_DAYS - 1L).atStartOfDay(IST).toInstant();

        // Pull the recent window once and aggregate in memory — small data set, avoids many queries.
        List<PageView> recent = pageViews.findByViewedAtAfter(windowStart);
        List<PageView> active = recent.stream()
                .filter(v -> v.getViewedAt().isAfter(activeSince))
                .toList();
        List<PageView> today = recent.stream()
                .filter(v -> v.getViewedAt().isAfter(dayStart))
                .toList();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalViews", pageViews.count());
        out.put("viewsToday", today.size());
        out.put("uniqueVisitorsToday", distinctCount(today, PageView::getSessionId));
        out.put("activeSessions", distinctCount(active, PageView::getSessionId));
        out.put("onlineCustomers", active.stream()
                .map(PageView::getUserId).filter(java.util.Objects::nonNull).distinct().count());
        out.put("topPages", topPages(today));
        out.put("devices", breakdown(today, PageView::getDevice));
        out.put("referrers", breakdown(today, PageView::getReferrer));
        out.put("dailyTraffic", dailyTraffic(recent));
        return out;
    }

    // ── aggregation helpers ───────────────────────────────────────────────
    private long distinctCount(List<PageView> views, java.util.function.Function<PageView, String> key) {
        return views.stream().map(key).filter(s -> s != null && !s.isBlank()).distinct().count();
    }

    private List<Map<String, Object>> topPages(List<PageView> views) {
        Map<String, Long> counts = views.stream()
                .collect(Collectors.groupingBy(v -> v.getPath() == null ? "/" : v.getPath(), Collectors.counting()));
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(TOP_PAGES)
                .map(e -> labelled("path", e.getKey(), e.getValue()))
                .toList();
    }

    private List<Map<String, Object>> breakdown(List<PageView> views, java.util.function.Function<PageView, String> key) {
        Map<String, Long> counts = views.stream()
                .collect(Collectors.groupingBy(v -> {
                    String k = key.apply(v);
                    return (k == null || k.isBlank()) ? "unknown" : k;
                }, Collectors.counting()));
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> labelled("label", e.getKey(), e.getValue()))
                .toList();
    }

    /** Views per day for the last {@link #TRAFFIC_DAYS} days, oldest first, gap-filled with zeros. */
    private List<Map<String, Object>> dailyTraffic(List<PageView> views) {
        Map<LocalDate, Long> byDay = views.stream()
                .collect(Collectors.groupingBy(v -> v.getViewedAt().atZone(IST).toLocalDate(), Collectors.counting()));
        List<Map<String, Object>> series = new ArrayList<>();
        LocalDate today = LocalDate.now(IST);
        for (int i = TRAFFIC_DAYS - 1; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", day.toString());
            point.put("views", byDay.getOrDefault(day, 0L));
            series.add(point);
        }
        return series;
    }

    private Map<String, Object> labelled(String keyName, String value, long count) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(keyName, value);
        m.put("count", count);
        return m;
    }

    // ── normalisation ─────────────────────────────────────────────────────
    private String normaliseDevice(String device) {
        if (device == null) {
            return "unknown";
        }
        String d = device.toLowerCase();
        if (d.equals("mobile") || d.equals("tablet") || d.equals("desktop")) {
            return d;
        }
        return "unknown";
    }

    /** Reduce a full referrer URL to its host, or "direct" when empty. */
    private String normaliseReferrer(String referrer) {
        if (referrer == null || referrer.isBlank()) {
            return "direct";
        }
        try {
            String host = URI.create(referrer).getHost();
            if (host == null || host.isBlank()) {
                return "direct";
            }
            return host.startsWith("www.") ? host.substring(4) : host;
        } catch (IllegalArgumentException e) {
            return "other";
        }
    }

    private String trim(String s, int max) {
        if (s == null) {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}
