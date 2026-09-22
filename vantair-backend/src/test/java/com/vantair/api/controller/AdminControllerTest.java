package com.vantair.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vantair.api.TestFixtures;
import com.vantair.api.config.ApiException;
import com.vantair.api.dto.Dtos.IssueRefundRequest;
import com.vantair.api.model.Ad;
import com.vantair.api.model.Order;
import com.vantair.api.model.OrderStatus;
import com.vantair.api.model.Product;
import com.vantair.api.model.RefundCoupon;
import com.vantair.api.model.User;
import com.vantair.api.service.AnalyticsService;
import com.vantair.api.service.CatalogService;
import com.vantair.api.service.EngagementService;
import com.vantair.api.service.OrderService;
import com.vantair.api.service.RefundCouponService;
import com.vantair.api.service.UserService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired MockMvc mvc;
    @MockBean OrderService orders;
    @MockBean CatalogService catalog;
    @MockBean UserService users;
    @MockBean EngagementService engagement;
    @MockBean RefundCouponService refunds;
    @MockBean AnalyticsService analytics;
    @MockBean com.vantair.api.service.AdService adsService;
    @MockBean com.vantair.api.service.ProductContentService productContent;

    @Test
    void stats_aggregates() throws Exception {
        Order paid = TestFixtures.order("VNT1", 1L, 1000, OrderStatus.Delivered);
        Order cancelled = TestFixtures.order("VNT2", 1L, 500, OrderStatus.Cancelled);
        Order nullTotal = TestFixtures.order("VNT3", 1L, 0, OrderStatus.Confirmed);
        nullTotal.setTotal(null);
        when(orders.listAll()).thenReturn(List.of(paid, cancelled, nullTotal));
        when(users.listAll()).thenReturn(List.of(TestFixtures.user(1L, "a@b.com")));
        when(catalog.listProducts(null)).thenReturn(List.of(TestFixtures.product("P001", "perfumes", 999)));

        mvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.revenue").value(1000)) // cancelled + null excluded
                .andExpect(jsonPath("$.totalOrders").value(3))
                .andExpect(jsonPath("$.delivered").value(1))
                .andExpect(jsonPath("$.customers").value(1))
                .andExpect(jsonPath("$.products").value(1));
    }

    @Test
    void orders_list() throws Exception {
        when(orders.listAll()).thenReturn(List.of());
        mvc.perform(get("/api/admin/orders")).andExpect(status().isOk());
    }

    @Test
    void customers_list() throws Exception {
        when(users.listAll()).thenReturn(List.of());
        mvc.perform(get("/api/admin/customers")).andExpect(status().isOk());
    }

    @Test
    void createProduct_201() throws Exception {
        when(catalog.createProduct(any(Product.class))).thenReturn(TestFixtures.product("P009", "perfumes", 500));
        mvc.perform(post("/api/admin/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"P009\",\"name\":\"New\",\"price\":500}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("P009"));
    }

    @Test
    void updateProduct_ok() throws Exception {
        when(catalog.updateProduct(eq("P001"), any(Product.class)))
                .thenReturn(TestFixtures.product("P001", "perfumes", 600));
        mvc.perform(put("/api/admin/products/P001").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"P001\",\"name\":\"Upd\",\"price\":600}"))
                .andExpect(status().isOk());
    }

    @Test
    void hideProduct_noContent() throws Exception {
        mvc.perform(delete("/api/admin/products/P001")).andExpect(status().isNoContent());
        verify(catalog).setActive("P001", false);
    }

    @Test
    void enquiryInboxes() throws Exception {
        when(engagement.listSubscribers()).thenReturn(List.of());
        when(engagement.listContacts()).thenReturn(List.of());
        when(engagement.listPartners()).thenReturn(List.of());
        mvc.perform(get("/api/admin/newsletter")).andExpect(status().isOk());
        mvc.perform(get("/api/admin/contacts")).andExpect(status().isOk());
        mvc.perform(get("/api/admin/partners")).andExpect(status().isOk());
    }

    @Test
    void issueRefund_201_flattensCustomer() throws Exception {
        User u = TestFixtures.user(1L, "a@b.com");
        RefundCoupon rc = TestFixtures.refundCoupon(7L, "REFABCDEF", 500, u);
        when(refunds.issue(any(IssueRefundRequest.class))).thenReturn(rc);

        mvc.perform(post("/api/admin/refunds").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.com\",\"amount\":500,\"reason\":\"x\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("REFABCDEF"))
                .andExpect(jsonPath("$.customerEmail").value("a@b.com"))
                .andExpect(jsonPath("$.customerName").value("Tester"));
    }

    @Test
    void issueRefund_unknownEmail_404() throws Exception {
        when(refunds.issue(any(IssueRefundRequest.class)))
                .thenThrow(ApiException.notFound("No customer found with email: x@b.com"));
        mvc.perform(post("/api/admin/refunds").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"x@b.com\",\"amount\":500}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void issueRefund_invalidAmount_validationError() throws Exception {
        mvc.perform(post("/api/admin/refunds").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.com\",\"amount\":0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refunds_list_handlesNullUser() throws Exception {
        RefundCoupon orphan = TestFixtures.refundCoupon(8L, "REFNULL01", 100, null);
        when(refunds.listAll()).thenReturn(List.of(orphan));
        mvc.perform(get("/api/admin/refunds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").doesNotExist())
                .andExpect(jsonPath("$[0].code").value("REFNULL01"));
    }

    @Test
    void analytics_dashboard() throws Exception {
        when(analytics.dashboard()).thenReturn(Map.of("totalViews", 5L, "activeSessions", 2L));
        mvc.perform(get("/api/admin/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalViews").value(5));
    }

    @Test
    void generateContent_returnsTaglineAndDescription() throws Exception {
        when(productContent.generate(eq("Vantair X"), eq("perfumes")))
                .thenReturn(Map.of("tagline", "Lovely.", "description", "A great product."));
        mvc.perform(post("/api/admin/products/generate-content").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Vantair X\",\"category\":\"perfumes\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagline").value("Lovely."));
    }

    @Test
    void generateContent_blankName_validationError() throws Exception {
        mvc.perform(post("/api/admin/products/generate-content").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ads_list() throws Exception {
        when(adsService.listAll()).thenReturn(List.of(new Ad()));
        mvc.perform(get("/api/admin/ads")).andExpect(status().isOk());
    }

    @Test
    void ads_create_201() throws Exception {
        Ad ad = new Ad();
        ad.setId(1L);
        ad.setTitle("Sale");
        when(adsService.create(any())).thenReturn(ad);
        mvc.perform(post("/api/admin/ads").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Sale\",\"slot\":\"home\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Sale"));
    }

    @Test
    void ads_create_blankTitle_validationError() throws Exception {
        mvc.perform(post("/api/admin/ads").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ads_update() throws Exception {
        when(adsService.update(eq(1L), any())).thenReturn(new Ad());
        mvc.perform(put("/api/admin/ads/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void ads_toggle() throws Exception {
        when(adsService.setActive(1L, false)).thenReturn(new Ad());
        mvc.perform(put("/api/admin/ads/1/active?active=false")).andExpect(status().isOk());
    }

    @Test
    void ads_delete_noContent() throws Exception {
        mvc.perform(delete("/api/admin/ads/1")).andExpect(status().isNoContent());
        verify(adsService).delete(1L);
    }
}
