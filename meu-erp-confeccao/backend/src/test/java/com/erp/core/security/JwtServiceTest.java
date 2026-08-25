package com.erp.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private UserDetailsImpl userDetails;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set the secret key via reflection as it normally comes from @Value
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 1 day in ms

        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Test User");
        usuario.setEmail("test@example.com");
        usuario.setSenha("password");
        usuario.setRole("ADMIN");
        usuario.setTenantId("tenant_test");

        userDetails = new UserDetailsImpl(usuario);
    }

    @Test
    void shouldGenerateValidToken() {
        String token = jwtService.generateToken(userDetails);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals("test@example.com", extractedUsername);
        
        String extractedTenantId = jwtService.extractTenantId(token);
        assertEquals("tenant_test", extractedTenantId);
        
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldExtractUsernameCorrectly() {
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        
        assertEquals("test@example.com", username);
    }

    @Test
    void shouldValidateTokenForCorrectUser() {
        String token = jwtService.generateToken(userDetails);
        
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldNotValidateTokenForIncorrectUser() {
        String token = jwtService.generateToken(userDetails);
        
        Usuario otherUser = new Usuario();
        otherUser.setEmail("other@example.com");
        otherUser.setSenha("password");
        otherUser.setRole("USER");
        UserDetailsImpl otherUserDetails = new UserDetailsImpl(otherUser);
        
        assertFalse(jwtService.isTokenValid(token, otherUserDetails));
    }
}
