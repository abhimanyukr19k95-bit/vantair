package com.vantair.api.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vantair.api.TestFixtures;
import com.vantair.api.dto.Dtos.CouponResult;
import com.vantair.api.service.CouponService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired MockMvc mvc;
    @MockBean CouponService coupons;

    @Test
    void list() throws Exception {
        when(coupons.listActive()).thenReturn(List.of(TestFixtures.coupon("VANTAIR10", "percent", 10, 500)));
        mvc.perform(get("/api/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("VANTAIR10"));
    }

    @Test
    void apply_valid() throws Exception {
        when(coupons.apply(eq("VANTAIR10"), anyInt()))
                .thenReturn(new CouponResult(true, 100, "ok", "VANTAIR10", "percent"));
        mvc.perform(post("/api/coupons/apply").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"VANTAIR10\",\"subtotal\":1000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.discount").value(100));
    }

    @Test
    void apply_blankCode_validationError() throws Exception {
        mvc.perform(post("/api/coupons/apply").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"\",\"subtotal\":1000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
