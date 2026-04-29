package com.github.jknetl.ec.security;

import com.github.jknetl.ec.data.model.AppUser;
import com.github.jknetl.ec.data.model.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private AppUser testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        Tenant tenant = new Tenant(UUID.randomUUID(), "Test Tenant", null);
        testUser = new AppUser();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("user@test.com");
        testUser.setRole("USER");
        testUser.setTenant(tenant);
    }

    @Test
    void generateToken_whenValidUser_shouldReturnNonNullToken() {
        String token = jwtService.generateToken(testUser);
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void extractUserId_whenValidToken_shouldReturnUserId() {
        String token = jwtService.generateToken(testUser);
        UUID extracted = jwtService.extractUserId(token);
        assertThat(extracted).isEqualTo(testUser.getId());
    }

    @Test
    void isTokenValid_whenValidToken_shouldReturnTrue() {
        String token = jwtService.generateToken(testUser);
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_whenTamperedToken_shouldReturnFalse() {
        String token = jwtService.generateToken(testUser);
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertThat(jwtService.isTokenValid(tampered)).isFalse();
    }
}
