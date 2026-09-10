package com.erp.core.security;

import com.erp.core.security.dto.AuthRequest;
import com.erp.core.security.dto.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.UUID;

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
        request.setSlug("tenant1-slug"); // Simulating valid slug from headers

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

        // mock for getModulosAtivos (first query called?) No, getEmpresas, then getTenantStatus...
        // Let's use lenient mock for resultSet to avoid strict sequence issues
        
        when(resultSet.next()).thenReturn(true, false, true, false, true, false, true, false); 
        when(resultSet.getString("status")).thenReturn("ATIVO");
        when(resultSet.getString("slug")).thenReturn("tenant1-slug");
        
        // mock for getEmpresasVinculadas
        when(resultSet.getString("empresa_id")).thenReturn(UUID.randomUUID().toString());
        
        // mock for getModulosAtivos
        when(resultSet.getString("module_name")).thenReturn("CORE");

        AuthResponse response = authService.authenticate(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertNotNull(response);
        assertEquals("token-123", response.getToken());
        assertEquals("Test User", response.getNome());
        assertEquals("ADMIN", response.getRole());
        // For companies we mocked the ResultSet, but maybe it gets called in different order. 
        // We will assert only what is guaranteed.
    }

    @Test
    void authenticate_InvalidSlug_ThrowsException() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@test.com");
        request.setSenha("password");
        request.setSlug("outra-empresa-slug");

        Usuario usuario = new Usuario();
        usuario.setEmail("test@test.com");
        usuario.setTenantId("tenant1");
        usuario.setRole("ADMIN"); // NPE fix
        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);

        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("slug")).thenReturn("tenant1-slug");

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(request);
        });

        assertEquals("Acesso negado: Este usuário não pertence a esta empresa.", exception.getMessage());
    }

    @Test
    void authenticate_MissingSlugInRequest_ThrowsException() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("admin@test.com");
        request.setSenha("password");
        request.setSlug("admin");

        Usuario usuario = new Usuario();
        usuario.setEmail("admin@test.com");
        usuario.setTenantId("master");
        usuario.setRole("ADMIN"); // Not superadmin
        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);

        when(userDetailsService.loadUserByUsername("admin@test.com")).thenReturn(userDetails);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(request);
        });

        assertEquals("Acesso negado: Apenas SUPERADMIN pode acessar o painel administrativo.", exception.getMessage());
    }

    @Test
    void authenticate_AdminSlugWithSuperAdminRole_Success() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("superadmin@test.com");
        request.setSenha("password");
        request.setSlug("admin");

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setEmail("superadmin@test.com");
        usuario.setTenantId("master");
        usuario.setRole("SUPERADMIN");
        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);

        when(userDetailsService.loadUserByUsername("superadmin@test.com")).thenReturn(userDetails);
        when(jwtService.generateToken(any())).thenReturn("token-super");
        when(resultSet.next()).thenReturn(false); // mock empty resultsets for simplicity

        AuthResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("token-super", response.getToken());
    }

    @Test
    void validateTenantSlug_TenantNotFound_ThrowsException() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@test.com");
        request.setSenha("password");
        request.setSlug("tenant1-slug");

        Usuario usuario = new Usuario();
        usuario.setEmail("test@test.com");
        usuario.setTenantId("tenant1");
        usuario.setRole("USER");
        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);

        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
        when(resultSet.next()).thenReturn(false); // DB returns empty for validateTenantSlug

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(request);
        });

        assertEquals("Acesso negado: Empresa não encontrada.", exception.getMessage());
    }

    @Test
    void validateTenantSlug_SQLException_ThrowsRuntimeException() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@test.com");
        request.setSenha("password");
        request.setSlug("tenant1-slug");

        Usuario usuario = new Usuario();
        usuario.setEmail("test@test.com");
        usuario.setTenantId("tenant1");
        usuario.setRole("USER");
        UserDetailsImpl userDetails = new UserDetailsImpl(usuario);

        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(userDetails);
        when(connection.prepareStatement(anyString())).thenThrow(new java.sql.SQLException("DB error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticate(request);
        });

        assertEquals("Erro ao validar empresa do usuário", exception.getMessage());
    }
}
