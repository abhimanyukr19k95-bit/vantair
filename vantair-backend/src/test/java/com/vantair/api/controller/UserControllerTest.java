package com.vantair.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import com.vantair.api.dto.Dtos.AddressRequest;
import com.vantair.api.dto.Dtos.ChangePasswordRequest;
import com.vantair.api.dto.Dtos.UpdateProfileRequest;
import com.vantair.api.model.User;
import com.vantair.api.service.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired MockMvc mvc;
    @MockBean UserService users;

    @Test
    void getById() throws Exception {
        when(users.get(1L)).thenReturn(TestFixtures.user(1L, "a@b.com"));
        mvc.perform(get("/api/users/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("a@b.com"));
    }

    @Test
    void getById_missing_404() throws Exception {
        when(users.get(9L)).thenThrow(ApiException.notFound("User not found: 9"));
        mvc.perform(get("/api/users/9")).andExpect(status().isNotFound());
    }

    @Test
    void updateProfile() throws Exception {
        when(users.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
                .thenReturn(TestFixtures.user(1L, "a@b.com"));
        mvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void changePassword_ok() throws Exception {
        doNothing().when(users).changePassword(eq(1L), any(ChangePasswordRequest.class));
        mvc.perform(post("/api/users/1/change-password").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"a\",\"newPassword\":\"b\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated."));
    }

    @Test
    void changePassword_wrong_badRequest() throws Exception {
        doThrow(ApiException.badRequest("Current password is incorrect."))
                .when(users).changePassword(eq(1L), any(ChangePasswordRequest.class));
        mvc.perform(post("/api/users/1/change-password").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"x\",\"newPassword\":\"y\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAddress() throws Exception {
        when(users.addAddress(eq(1L), any(AddressRequest.class))).thenReturn(TestFixtures.user(1L, "a@b.com"));
        mvc.perform(post("/api/users/1/addresses").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"T\",\"line1\":\"1 Rd\",\"city\":\"Kol\",\"pincode\":\"700001\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void removeAddress() throws Exception {
        when(users.removeAddress(1L, 2L)).thenReturn(TestFixtures.user(1L, "a@b.com"));
        mvc.perform(delete("/api/users/1/addresses/2")).andExpect(status().isOk());
        verify(users).removeAddress(1L, 2L);
    }

    @Test
    void setDefaultAddress() throws Exception {
        when(users.setDefaultAddress(1L, 2L)).thenReturn(TestFixtures.user(1L, "a@b.com"));
        mvc.perform(put("/api/users/1/addresses/2/default")).andExpect(status().isOk());
    }

    @Test
    void toggleWishlist() throws Exception {
        when(users.toggleWishlist(1L, "P001")).thenReturn(List.of("P001"));
        mvc.perform(post("/api/users/1/wishlist/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("P001"));
    }
}
