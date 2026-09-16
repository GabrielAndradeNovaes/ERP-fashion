package com.erp.core.security.dto;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class UsuarioDTO {
    private UUID id;
    private String nome;
    private String email;
    private String role;
    private Boolean ativo;
    private String tenantId;
    private LocalDateTime criadoEm;
    private UUID filialPrincipalId;
    private List<String> permissoes;
    private List<EmpresaSimpleDTO> empresas;

    private String cpf;
    private String telefone;
    private String cargo;
    private LocalDate dataNascimento;
    private String departamento;
    private String fotoUrl;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public List<EmpresaSimpleDTO> getEmpresas() { return empresas; }
    public void setEmpresas(List<EmpresaSimpleDTO> empresas) { this.empresas = empresas; }

    public UUID getFilialPrincipalId() { return filialPrincipalId; }
    public void setFilialPrincipalId(UUID filialPrincipalId) { this.filialPrincipalId = filialPrincipalId; }

    public List<String> getPermissoes() { return permissoes; }
    public void setPermissoes(List<String> permissoes) { this.permissoes = permissoes; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}
