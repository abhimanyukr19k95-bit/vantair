package com.vantair.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vantair.api.dto.Dtos.TrackEventRequest;
import com.vantair.api.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {

    @Autowired MockMvc mvc;
    @MockBean AnalyticsService analytics;

    @Test
    void track_accepted() throws Exception {
        mvc.perform(post("/api/analytics/track").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"path\":\"/shop.html\",\"sessionId\":\"s1\",\"device\":\"mobile\"}"))
                .andExpect(status().isAccepted());
        verify(analytics).track(any(TrackEventRequest.class));
    }

    @Test
    void track_blankPath_validationError() throws Exception {
        mvc.perform(post("/api/analytics/track").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"path\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
