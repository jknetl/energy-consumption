package com.github.jknetl.ec.rest;

import tools.jackson.databind.ObjectMapper;
import com.github.jknetl.ec.rest.dto.LoginRequest;
import com.github.jknetl.ec.rest.dto.LoginResponse;
import com.github.jknetl.ec.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest enables Spring Security auto-configuration; @WithMockUser satisfies it
// without importing the full SecurityConfig (which would need AppUserRepository etc.)
@WebMvcTest(AuthController.class)
@WithMockUser
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockitoBean AuthService authService;

    @Test
    void login_whenValidCredentials_shouldReturn200WithToken() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponse("jwt-token-value"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("user@test.com", "secret"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token-value"));
    }

    @Test
    void login_whenInvalidCredentials_shouldReturn401() throws Exception {
        when(authService.login(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("user@test.com", "wrong"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_whenMissingEmail_shouldReturn400() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("", "secret"))))
                .andExpect(status().isBadRequest());
    }
}
