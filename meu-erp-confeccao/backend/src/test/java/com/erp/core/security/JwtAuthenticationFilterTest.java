package com.erp.core.security;

import com.erp.core.tenant.EmpresaContext;
import com.erp.core.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
        EmpresaContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_NoAuthHeader_ContinuesChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-TenantID")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("master", TenantContext.getCurrentTenant());
    }

    @Test
    void doFilterInternal_NoAuthHeader_WithXTenantId_SetsTenantAndContinues() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-TenantID")).thenReturn("custom-tenant");

        doAnswer(invocation -> {
            assertEquals("custom-tenant", TenantContext.getCurrentTenant());
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ValidToken_SetsAuthenticationAndContext() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.token.here");
        when(jwtService.extractUsername("valid.token.here")).thenReturn("user@test.com");
        when(jwtService.extractTenantId("valid.token.here")).thenReturn("tenant_123");
        
        String empresaId = UUID.randomUUID().toString();
        when(jwtService.extractEmpresas("valid.token.here")).thenReturn(Collections.singletonList(empresaId));

        Usuario usuario = new Usuario();
        usuario.setEmail("user@test.com");
        usuario.setRole("USER");
        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);
        
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid.token.here", userDetails)).thenReturn(true);

        doAnswer(invocation -> {
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            assertEquals("tenant_123", TenantContext.getCurrentTenant());
            assertNotNull(EmpresaContext.getEmpresas());
            assertEquals(1, EmpresaContext.getEmpresas().size());
            assertEquals(empresaId, EmpresaContext.getEmpresas().get(0).toString());
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ValidToken_SuperAdminOverridesTenant() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer superadmin.token.here");
        when(request.getHeader("X-TenantID")).thenReturn("overridden_tenant");
        when(jwtService.extractUsername("superadmin.token.here")).thenReturn("admin@test.com");
        when(jwtService.extractTenantId("superadmin.token.here")).thenReturn("master");

        Usuario usuario = new Usuario();
        usuario.setEmail("admin@test.com");
        usuario.setRole("SUPERADMIN");
        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);
        
        when(userDetailsService.loadUserByUsername("admin@test.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("superadmin.token.here", userDetails)).thenReturn(true);

        doAnswer(invocation -> {
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            assertEquals("overridden_tenant", TenantContext.getCurrentTenant());
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
