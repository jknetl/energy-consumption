package com.github.jknetl.ec.security;

import com.github.jknetl.ec.data.model.AppUser;
import com.github.jknetl.ec.data.model.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private AppUser testUser;

    @BeforeEach
    void setUp() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair keyPair = gen.generateKeyPair();

        String privatePem = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(keyPair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----";
        String publicPem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(keyPair.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----";

        jwtService = new JwtService(privatePem, publicPem);
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
