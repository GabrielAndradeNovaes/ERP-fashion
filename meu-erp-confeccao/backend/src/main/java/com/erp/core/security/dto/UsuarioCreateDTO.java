package com.erp.core.security.dto;

import java.util.List;
import java.util.UUID;

public class UsuarioCreateDTO {
    private String nome;
    private String email;
    private String senha;
    private String role;
    private UUID filialPrincipalId;
    private List<String> permissoes;
    private List<UUID> empresaIds;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public UUID getFilialPrincipalId() { return filialPrincipalId; }
    public void setFilialPrincipalId(UUID filialPrincipalId) { this.filialPrincipalId = filialPrincipalId; }

    public List<String> getPermissoes() { return permissoes; }
    public void setPermissoes(List<String> permissoes) { this.permissoes = permissoes; }

    public List<UUID> getEmpresaIds() { return empresaIds; }
    public void setEmpresaIds(List<UUID> empresaIds) { this.empresaIds = empresaIds; }
}
