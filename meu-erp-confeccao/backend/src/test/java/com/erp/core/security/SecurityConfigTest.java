package com.erp.core.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthFilter;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void testAuthenticationProvider() {
        AuthenticationProvider provider = securityConfig.authenticationProvider();
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }

    @Test
    void testAuthenticationManager() throws Exception {
        AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
        AuthenticationManager managerMock = mock(AuthenticationManager.class);
        when(config.getAuthenticationManager()).thenReturn(managerMock);

        AuthenticationManager manager = securityConfig.authenticationManager(config);
        assertNotNull(manager);
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
    }

    @Test
    void testCorsConfigurationSource() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertNotNull(source);
    }
}
