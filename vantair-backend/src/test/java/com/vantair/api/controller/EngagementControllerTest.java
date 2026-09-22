package com.vantair.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vantair.api.dto.Dtos.ContactRequest;
import com.vantair.api.dto.Dtos.PartnerRequest;
import com.vantair.api.model.ContactMessage;
import com.vantair.api.model.PartnerEnquiry;
import com.vantair.api.service.EngagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EngagementController.class)
class EngagementControllerTest {

    @Autowired MockMvc mvc;
    @MockBean EngagementService engagement;

    @Test
    void newsletter_ok() throws Exception {
        mvc.perform(post("/api/newsletter").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Subscribed."));
        verify(engagement).subscribe("a@b.com");
    }

    @Test
    void newsletter_invalidEmail_validationError() throws Exception {
        mvc.perform(post("/api/newsletter").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"bad\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void contact_created() throws Exception {
        when(engagement.submitContact(any(ContactRequest.class))).thenReturn(new ContactMessage());
        mvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"R\",\"email\":\"r@b.com\",\"message\":\"hi\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Message received."));
    }

    @Test
    void partner_created() throws Exception {
        when(engagement.submitPartner(any(PartnerRequest.class))).thenReturn(new PartnerEnquiry());
        mvc.perform(post("/api/partner").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"A\",\"email\":\"a@m.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Enquiry received."));
    }
}
