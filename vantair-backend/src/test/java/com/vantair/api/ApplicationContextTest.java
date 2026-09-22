package com.vantair.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vantair.api.config.WebConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Boots the whole application against H2 — verifies the context wires up
 * (controllers, services, JPA, seeder) and that the CORS config from
 * {@link WebConfig} is applied to /api/** requests.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApplicationContextTest {

    @Autowired ApplicationContext context;
    @Autowired MockMvc mvc;

    @Test
    void contextLoads_withKeyBeans() {
        assertThat(context.getBean(WebConfig.class)).isNotNull();
        assertThat(context.containsBean("vantairOpenApi")).isTrue();
    }

    @Test
    void seededCatalog_isServed() throws Exception {
        // DataSeeder runs on startup, so the catalog endpoints return data.
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/products"))
                .andExpect(status().isOk());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/categories"))
                .andExpect(status().isOk());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/coupons"))
                .andExpect(status().isOk());
    }

    @Test
    void cors_preflight_isAllowedForConfiguredOrigin() throws Exception {
        mvc.perform(options("/api/products")
                        .header("Origin", "http://localhost:5500")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5500"));
    }
}
