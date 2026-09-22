package com.vantair.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vantair.api.TestFixtures;
import com.vantair.api.config.ApiException;
import com.vantair.api.dto.Dtos.SignInRequest;
import com.vantair.api.dto.Dtos.SignUpRequest;
import com.vantair.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @MockBean UserService users;

    @Test
    void signup_created() throws Exception {
        when(users.signUp(any(SignUpRequest.class))).thenReturn(TestFixtures.user(1L, "a@b.com"));
        mvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tester\",\"email\":\"a@b.com\",\"password\":\"pw\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("a@b.com"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void signup_invalidEmail_validationError() throws Exception {
        mvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tester\",\"email\":\"not-an-email\",\"password\":\"pw\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signin_ok() throws Exception {
        when(users.signIn(any(SignInRequest.class))).thenReturn(TestFixtures.user(1L, "a@b.com"));
        mvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.com\",\"password\":\"pw\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void signin_badCredentials_401() throws Exception {
        when(users.signIn(any(SignInRequest.class))).thenThrow(ApiException.unauthorized("Incorrect password."));
        mvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.com\",\"password\":\"bad\"}"))
                .andExpect(status().isUnauthorized());
    }
}
