package com.vantair.api.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vantair.api.model.Ad;
import com.vantair.api.service.AdService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdController.class)
class AdControllerTest {

    @Autowired MockMvc mvc;
    @MockBean AdService ads;

    @Test
    void active_noSlot() throws Exception {
        Ad a = new Ad();
        a.setTitle("Sale");
        when(ads.listActive(isNull())).thenReturn(List.of(a));
        mvc.perform(get("/api/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sale"));
    }

    @Test
    void active_withSlot() throws Exception {
        when(ads.listActive(eq("home"))).thenReturn(List.of());
        mvc.perform(get("/api/ads?slot=home")).andExpect(status().isOk());
    }
}
