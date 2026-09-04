package com.erp.core.security;

import com.erp.core.security.dto.AuthRequest;
import com.erp.core.security.dto.AuthResponse;
import com.erp.core.security.UserDetailsServiceImpl;
import com.erp.core.security.JwtService;
import com.erp.core.security.Usuario;
import com.erp.core.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    void authenticate_ValidCredentials_ReturnsAuthResponse() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@test.com");
        request.setSenha("password");

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Test User");
        usuario.setEmail("test@test.com");
        usuario.setRole("ADMIN");
        usuario.setTenantId("tenant1");
        usuario.setFilialPrincipalId(UUID.randomUUID());
        usuario.setPermissoes(new java.util.HashSet<>(Arrays.asList("PERM1", "PERM2")));

        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);

        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
        when(jwtService.generateToken(any())).thenReturn("token-123");

        // mock for getTenantStatus
        when(resultSet.next()).thenReturn(true, false, true, false); // first next() is for status, then false. Next is for empresas.
        when(resultSet.getString("status")).thenReturn("ATIVO");
        when(resultSet.getString("empresa_id")).thenReturn(UUID.randomUUID().toString());

        AuthResponse response = authService.authenticate(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertNotNull(response);
        assertEquals("token-123", response.getToken());
        assertEquals("Test User", response.getNome());
        assertEquals("ADMIN", response.getRole());
        assertEquals("ATIVO", response.getTenantStatus());
        assertEquals(1, response.getEmpresas().size());
        assertTrue(response.getPermissoes().contains("PERM1"));
    }
}
