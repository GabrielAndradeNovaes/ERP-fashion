package com.erp.core.controller;

import com.erp.core.security.Usuario;
import com.erp.core.security.UsuarioRepository;
import com.erp.core.security.UsuarioEmpresa;
import com.erp.core.security.UsuarioEmpresaRepository;
import com.erp.core.repository.EmpresaRepository;
import com.erp.core.tenant.TenantContext;
import com.erp.core.security.dto.UsuarioDTO;
import com.erp.core.security.dto.UsuarioCreateDTO;
import com.erp.core.security.dto.EmpresaSimpleDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, 
                             UsuarioEmpresaRepository usuarioEmpresaRepository,
                             EmpresaRepository empresaRepository,
                             PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioEmpresaRepository = usuarioEmpresaRepository;
        this.empresaRepository = empresaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getTenantId().equals(TenantContext.getCurrentTenant()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @PostMapping
    public ResponseEntity<UsuarioDTO> criarUsuario(@RequestBody UsuarioCreateDTO dto) {
        Usuario u = new Usuario();
        u.setNome(dto.getNome());
        u.setEmail(dto.getEmail());
        u.setSenha(passwordEncoder.encode(dto.getSenha()));
        if ("SUPERADMIN".equalsIgnoreCase(dto.getRole())) {
            throw new IllegalArgumentException("Não é permitido atribuir a role SUPERADMIN.");
        }
        u.setRole(dto.getRole());
        u.setAtivo(true);
        u.setTenantId(TenantContext.getCurrentTenant());
        u.setCriadoEm(LocalDateTime.now());
        u.setFilialPrincipalId(dto.getFilialPrincipalId());
        if (dto.getPermissoes() != null) {
            u.setPermissoes(new HashSet<>(dto.getPermissoes()));
        }
        u.setCpf(dto.getCpf());
        u.setTelefone(dto.getTelefone());
        u.setCargo(dto.getCargo());
        u.setDataNascimento(dto.getDataNascimento());
        u.setDepartamento(dto.getDepartamento());
        u.setFotoUrl(dto.getFotoUrl());
        
        Usuario salvo = usuarioRepository.save(u);
        
        atribuirEmpresas(salvo.getId(), dto.getEmpresaIds());
        
        return ResponseEntity.ok(toDTO(salvo));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(@PathVariable UUID id, @RequestBody UsuarioCreateDTO dto) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        
        Usuario u = opt.get();
        if (!u.getTenantId().equals(TenantContext.getCurrentTenant())) return ResponseEntity.status(403).build();

        u.setNome(dto.getNome());
        u.setEmail(dto.getEmail());
        if ("SUPERADMIN".equalsIgnoreCase(dto.getRole())) {
            throw new IllegalArgumentException("Não é permitido atribuir a role SUPERADMIN.");
        }
        u.setRole(dto.getRole());
        u.setFilialPrincipalId(dto.getFilialPrincipalId());
        if (dto.getPermissoes() != null) {
            u.setPermissoes(new HashSet<>(dto.getPermissoes()));
        }
        
        u.setCpf(dto.getCpf());
        u.setTelefone(dto.getTelefone());
        u.setCargo(dto.getCargo());
        u.setDataNascimento(dto.getDataNascimento());
        u.setDepartamento(dto.getDepartamento());
        u.setFotoUrl(dto.getFotoUrl());

        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            u.setSenha(passwordEncoder.encode(dto.getSenha()));
        }
        
        Usuario salvo = usuarioRepository.save(u);
        
        atribuirEmpresas(salvo.getId(), dto.getEmpresaIds());
        
        return ResponseEntity.ok(toDTO(salvo));
    }

    @Transactional
    @PostMapping("/{usuarioId}/empresas")
    public ResponseEntity<Void> salvarEmpresas(@PathVariable UUID usuarioId, @RequestBody List<UUID> empresaIds) {
        atribuirEmpresas(usuarioId, empresaIds);
        return ResponseEntity.ok().build();
    }
    
    private void atribuirEmpresas(UUID usuarioId, List<UUID> empresaIds) {
        if (empresaIds == null) return;
        List<UsuarioEmpresa> antigas = usuarioEmpresaRepository.findByUsuarioId(usuarioId);
        usuarioEmpresaRepository.deleteAll(antigas);

        List<UsuarioEmpresa> novas = empresaIds.stream().map(empId -> {
            UsuarioEmpresa ue = new UsuarioEmpresa();
            ue.setUsuarioId(usuarioId);
            ue.setEmpresaId(empId);
            return ue;
        }).collect(Collectors.toList());
        
        usuarioEmpresaRepository.saveAll(novas);
    }

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNome(u.getNome());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole());
        dto.setAtivo(u.getAtivo());
        dto.setTenantId(u.getTenantId());
        dto.setCriadoEm(u.getCriadoEm());
        dto.setFilialPrincipalId(u.getFilialPrincipalId());
        if (u.getPermissoes() != null) {
            dto.setPermissoes(u.getPermissoes().stream().collect(Collectors.toList()));
        }
        dto.setCpf(u.getCpf());
        dto.setTelefone(u.getTelefone());
        dto.setCargo(u.getCargo());
        dto.setDataNascimento(u.getDataNascimento());
        dto.setDepartamento(u.getDepartamento());
        dto.setFotoUrl(u.getFotoUrl());
        
        List<UUID> empIds = usuarioEmpresaRepository.findByUsuarioId(u.getId())
                .stream().map(UsuarioEmpresa::getEmpresaId).collect(Collectors.toList());
                
        List<EmpresaSimpleDTO> empresas = empresaRepository.findAllById(empIds).stream()
            .map(e -> new EmpresaSimpleDTO(e.getId(), e.getNomeFantasia() != null ? e.getNomeFantasia() : e.getRazaoSocial()))
            .collect(Collectors.toList());
            
        dto.setEmpresas(empresas);
        return dto;
    }
}
