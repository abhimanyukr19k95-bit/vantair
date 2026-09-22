package com.vantair.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vantair.api.TestFixtures;
import com.vantair.api.config.ApiException;
import com.vantair.api.model.Category;
import com.vantair.api.service.CatalogService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CatalogController.class)
class CatalogControllerTest {

    @Autowired MockMvc mvc;
    @MockBean CatalogService catalog;

    @Test
    void products_noCategory() throws Exception {
        when(catalog.listProducts(isNull())).thenReturn(List.of(TestFixtures.product("P001", "perfumes", 999)));
        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("P001"));
    }

    @Test
    void products_withCategory() throws Exception {
        when(catalog.listProducts(eq("perfumes"))).thenReturn(List.of());
        mvc.perform(get("/api/products?category=perfumes")).andExpect(status().isOk());
    }

    @Test
    void product_byId() throws Exception {
        when(catalog.getProduct("P001")).thenReturn(TestFixtures.product("P001", "perfumes", 999));
        mvc.perform(get("/api/products/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(999));
    }

    @Test
    void product_missing_returns404() throws Exception {
        when(catalog.getProduct("X")).thenThrow(ApiException.notFound("Product not found: X"));
        mvc.perform(get("/api/products/X"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found: X"));
    }

    @Test
    void categories() throws Exception {
        Category c = new Category();
        c.setId("perfumes");
        c.setLabel("Perfumes");
        when(catalog.listCategories()).thenReturn(List.of(c));
        mvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("perfumes"));
    }
}
