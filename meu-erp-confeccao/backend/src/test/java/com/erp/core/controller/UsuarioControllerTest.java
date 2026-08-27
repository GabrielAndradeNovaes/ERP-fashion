package com.erp.core.controller;

import com.erp.core.domain.Empresa;
import com.erp.core.repository.EmpresaRepository;
import com.erp.core.security.Usuario;
import com.erp.core.security.UsuarioEmpresa;
import com.erp.core.security.UsuarioEmpresaRepository;
import com.erp.core.security.UsuarioRepository;
import com.erp.core.security.dto.UsuarioCreateDTO;
import com.erp.core.security.dto.UsuarioDTO;
import com.erp.core.tenant.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UsuarioEmpresaRepository usuarioEmpresaRepository;
    @Mock
    private EmpresaRepository empresaRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioController controller;

    @BeforeEach
    void setUp() {
        TenantContext.setCurrentTenant("test_tenant");
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void testListarUsuarios() {
        Usuario u = new Usuario();
        u.setId(UUID.randomUUID());
        u.setTenantId("test_tenant");
        u.setPermissoes(new HashSet<>(Arrays.asList("PERM1")));
        
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(u));
        when(usuarioEmpresaRepository.findByUsuarioId(u.getId())).thenReturn(Collections.emptyList());
        when(empresaRepository.findAllById(any())).thenReturn(Collections.emptyList());

        List<UsuarioDTO> result = controller.listarUsuarios();
        assertEquals(1, result.size());
    }

    @Test
    void testCriarUsuario() {
        UsuarioCreateDTO dto = new UsuarioCreateDTO();
        dto.setNome("Test");
        dto.setSenha("123");
        dto.setPermissoes(Arrays.asList("PERM1"));
        dto.setEmpresaIds(Collections.singletonList(UUID.randomUUID()));

        Usuario u = new Usuario();
        u.setId(UUID.randomUUID());
        u.setTenantId("test_tenant");

        when(passwordEncoder.encode("123")).thenReturn("encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(u);
        when(usuarioEmpresaRepository.findByUsuarioId(u.getId())).thenReturn(Collections.emptyList());
        when(empresaRepository.findAllById(any())).thenReturn(Collections.emptyList());

        ResponseEntity<UsuarioDTO> result = controller.criarUsuario(dto);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        verify(usuarioEmpresaRepository).saveAll(any());
    }

    @Test
    void testAtualizarUsuario_Found() {
        UUID id = UUID.randomUUID();
        UsuarioCreateDTO dto = new UsuarioCreateDTO();
        dto.setSenha("new");
        dto.setPermissoes(Arrays.asList("PERM2"));
        dto.setEmpresaIds(Collections.singletonList(UUID.randomUUID()));

        Usuario u = new Usuario();
        u.setId(id);
        u.setTenantId("test_tenant");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(u));
        when(passwordEncoder.encode("new")).thenReturn("encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(u);
        
        ResponseEntity<UsuarioDTO> result = controller.atualizarUsuario(id, dto);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(usuarioEmpresaRepository).saveAll(any());
    }

    @Test
    void testAtualizarUsuario_NotFound() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<UsuarioDTO> result = controller.atualizarUsuario(id, new UsuarioCreateDTO());
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void testAtualizarUsuario_WrongTenant() {
        UUID id = UUID.randomUUID();
        Usuario u = new Usuario();
        u.setTenantId("other_tenant");
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(u));
        
        ResponseEntity<UsuarioDTO> result = controller.atualizarUsuario(id, new UsuarioCreateDTO());
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    void testSalvarEmpresas() {
        UUID id = UUID.randomUUID();
        List<UUID> empIds = Arrays.asList(UUID.randomUUID());
        when(usuarioEmpresaRepository.findByUsuarioId(id)).thenReturn(Collections.emptyList());
        
        ResponseEntity<Void> result = controller.salvarEmpresas(id, empIds);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(usuarioEmpresaRepository).saveAll(anyList());
    }
}
