package com.vantair.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vantair.api.TestFixtures;
import com.vantair.api.config.ApiException;
import com.vantair.api.model.OrderStatus;
import com.vantair.api.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired MockMvc mvc;
    @MockBean OrderService orders;

    @Test
    void validate_ok() throws Exception {
        when(orders.validateDeliveryOtp("VNT1", "123456"))
                .thenReturn(TestFixtures.order("VNT1", 1L, 999, OrderStatus.Delivered));
        mvc.perform(post("/api/delivery/VNT1/validate-otp").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"otp\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Delivered"));
    }

    @Test
    void validate_wrongOtp_badRequest() throws Exception {
        when(orders.validateDeliveryOtp("VNT1", "000000"))
                .thenThrow(ApiException.badRequest("Invalid OTP. Do not deliver."));
        mvc.perform(post("/api/delivery/VNT1/validate-otp").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"otp\":\"000000\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validate_blankOtp_validationError() throws Exception {
        mvc.perform(post("/api/delivery/VNT1/validate-otp").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"otp\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
