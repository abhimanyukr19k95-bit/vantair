package com.vantair.api.controller;

import com.vantair.api.dto.Dtos.AdRequest;
import com.vantair.api.dto.Dtos.GenerateContentRequest;
import com.vantair.api.dto.Dtos.IssueRefundRequest;
import com.vantair.api.dto.Dtos.RefundCouponResponse;
import com.vantair.api.model.Ad;
import com.vantair.api.model.ContactMessage;
import com.vantair.api.model.NewsletterSubscriber;
import com.vantair.api.model.Order;
import com.vantair.api.model.OrderStatus;
import com.vantair.api.model.PartnerEnquiry;
import com.vantair.api.model.Product;
import com.vantair.api.model.RefundCoupon;
import com.vantair.api.model.User;
import com.vantair.api.service.AdService;
import com.vantair.api.service.AnalyticsService;
import com.vantair.api.service.CatalogService;
import com.vantair.api.service.EngagementService;
import com.vantair.api.service.OrderService;
import com.vantair.api.service.ProductContentService;
import com.vantair.api.service.RefundCouponService;
import com.vantair.api.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Backs the admin panel (admin.html): dashboard stats, order/product/customer
 * management, and the enquiry inboxes.
 *
 * <p>NOTE: these endpoints are currently unauthenticated (auth deferred). They
 * must be protected before any real deployment.
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Dashboard stats, order/product/customer management (UNSECURED — see note)")
public class AdminController {

    private final OrderService orders;
    private final CatalogService catalog;
    private final UserService users;
    private final EngagementService engagement;
    private final RefundCouponService refunds;
    private final AnalyticsService analytics;
    private final AdService adsService;
    private final ProductContentService productContent;

    public AdminController(OrderService orders, CatalogService catalog, UserService users,
                           EngagementService engagement, RefundCouponService refunds,
                           AnalyticsService analytics, AdService adsService,
                           ProductContentService productContent) {
        this.orders = orders;
        this.catalog = catalog;
        this.users = users;
        this.engagement = engagement;
        this.refunds = refunds;
        this.analytics = analytics;
        this.adsService = adsService;
        this.productContent = productContent;
    }

    // ── Dashboard ───────────────────────────────────────────────────────
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        List<Order> all = orders.listAll();
        long revenue = all.stream()
                .filter(o -> o.getStatus() != OrderStatus.Cancelled)
                .mapToLong(o -> o.getTotal() == null ? 0 : o.getTotal())
                .sum();
        long delivered = all.stream().filter(o -> o.getStatus() == OrderStatus.Delivered).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("revenue", revenue);
        stats.put("totalOrders", all.size());
        stats.put("delivered", delivered);
        stats.put("customers", users.listAll().size());
        stats.put("products", catalog.listProducts(null).size());
        return stats;
    }

    // ── Orders ──────────────────────────────────────────────────────────
    @GetMapping("/orders")
    public List<Order> orders() {
        return orders.listAll();
    }

    // ── Customers ───────────────────────────────────────────────────────
    @GetMapping("/customers")
    public List<User> customers() {
        return users.listAll();
    }

    // ── Products ────────────────────────────────────────────────────────
    @PostMapping("/products")
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalog.createProduct(product));
    }

    @PutMapping("/products/{id}")
    public Product update(@PathVariable String id, @RequestBody Product product) {
        return catalog.updateProduct(id, product);
    }

    /** Soft-hide a product (sets active=false) — matches the admin "Hide" action. */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> hide(@PathVariable String id) {
        catalog.setActive(id, false);
        return ResponseEntity.noContent().build();
    }

    // ── Enquiries ───────────────────────────────────────────────────────
    @GetMapping("/newsletter")
    public List<NewsletterSubscriber> subscribers() {
        return engagement.listSubscribers();
    }

    @GetMapping("/contacts")
    public List<ContactMessage> contacts() {
        return engagement.listContacts();
    }

    @GetMapping("/partners")
    public List<PartnerEnquiry> partners() {
        return engagement.listPartners();
    }

    // ── Refunds ─────────────────────────────────────────────────────────
    @PostMapping("/refunds")
    public ResponseEntity<RefundCouponResponse> issueRefund(@Valid @RequestBody IssueRefundRequest req) {
        RefundCoupon issued = refunds.issue(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(toRefundResponse(issued));
    }

    @GetMapping("/refunds")
    public List<RefundCouponResponse> refunds() {
        return refunds.listAll().stream().map(this::toRefundResponse).toList();
    }

    // ── Analytics ───────────────────────────────────────────────────────
    @GetMapping("/analytics")
    public Map<String, Object> analytics() {
        return analytics.dashboard();
    }

    // ── Product content generation ──────────────────────────────────────
    @PostMapping("/products/generate-content")
    public Map<String, String> generateContent(@Valid @RequestBody GenerateContentRequest req) {
        return productContent.generate(req.name(), req.category());
    }

    // ── Ads ─────────────────────────────────────────────────────────────
    @GetMapping("/ads")
    public List<Ad> listAds() {
        return adsService.listAll();
    }

    @PostMapping("/ads")
    public ResponseEntity<Ad> createAd(@Valid @RequestBody AdRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adsService.create(req));
    }

    @PutMapping("/ads/{id}")
    public Ad updateAd(@PathVariable Long id, @Valid @RequestBody AdRequest req) {
        return adsService.update(id, req);
    }

    @PutMapping("/ads/{id}/active")
    public Ad toggleAd(@PathVariable Long id, @RequestParam boolean active) {
        return adsService.setActive(id, active);
    }

    @DeleteMapping("/ads/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Long id) {
        adsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private RefundCouponResponse toRefundResponse(RefundCoupon c) {
        User u = c.getUser();
        return new RefundCouponResponse(
                c.getId(), c.getCode(), c.getValue(), c.getReason(), c.isUsed(),
                c.getIssuedAt() == null ? null : c.getIssuedAt().toString(),
                u == null ? null : u.getName(),
                u == null ? null : u.getEmail());
    }
}
