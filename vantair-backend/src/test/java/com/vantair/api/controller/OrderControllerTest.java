package com.vantair.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vantair.api.TestFixtures;
import com.vantair.api.config.ApiException;
import com.vantair.api.dto.Dtos.PlaceOrderRequest;
import com.vantair.api.model.OrderStatus;
import com.vantair.api.service.OrderService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired MockMvc mvc;
    @MockBean OrderService orders;

    private static final String VALID_ORDER = """
            {"userId":1,"addressId":1,"payment":"upi","items":[{"productId":"P001","variantIndex":0,"qty":1}]}""";

    @Test
    void place_created() throws Exception {
        when(orders.placeOrder(any(PlaceOrderRequest.class)))
                .thenReturn(TestFixtures.order("VNT1", 1L, 999, OrderStatus.Confirmed));
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).content(VALID_ORDER))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("VNT1"));
    }

    @Test
    void place_missingItems_validationError() throws Exception {
        mvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"addressId\":1,\"payment\":\"upi\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void list_all() throws Exception {
        when(orders.listAll()).thenReturn(List.of());
        mvc.perform(get("/api/orders")).andExpect(status().isOk());
    }

    @Test
    void list_byUser() throws Exception {
        when(orders.listForUser(1L)).thenReturn(List.of());
        mvc.perform(get("/api/orders?userId=1")).andExpect(status().isOk());
    }

    @Test
    void getById() throws Exception {
        when(orders.get("VNT1")).thenReturn(TestFixtures.order("VNT1", 1L, 999, OrderStatus.Confirmed));
        mvc.perform(get("/api/orders/VNT1")).andExpect(status().isOk());
    }

    @Test
    void getById_missing_404() throws Exception {
        when(orders.get("X")).thenThrow(ApiException.notFound("Order not found: X"));
        mvc.perform(get("/api/orders/X")).andExpect(status().isNotFound());
    }

    @Test
    void updateStatus() throws Exception {
        when(orders.updateStatus(eq("VNT1"), eq("Shipped")))
                .thenReturn(TestFixtures.order("VNT1", 1L, 999, OrderStatus.Shipped));
        mvc.perform(put("/api/orders/VNT1/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"Shipped\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void revealOtp() throws Exception {
        when(orders.revealOtp("VNT1", 1L)).thenReturn("123456");
        mvc.perform(post("/api/orders/VNT1/reveal-otp?userId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otp").value("123456"));
    }
}
