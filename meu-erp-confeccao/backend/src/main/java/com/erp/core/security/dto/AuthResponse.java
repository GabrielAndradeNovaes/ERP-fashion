package com.erp.core.security.dto;

public class AuthResponse {
    private String token;
    private String nome;
    private String email;
    private String role;
    private String tenantId;
    private String tenantStatus;
    private java.util.List<String> empresas;
    private String filialPrincipalId;
    private java.util.List<String> permissoes;

    public AuthResponse(String token, String nome, String email, String role, String tenantId, String tenantStatus, java.util.List<String> empresas, String filialPrincipalId, java.util.List<String> permissoes) {
        this.token = token;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.tenantId = tenantId;
        this.tenantStatus = tenantStatus;
        this.empresas = empresas;
        this.filialPrincipalId = filialPrincipalId;
        this.permissoes = permissoes;
    }

    public String getToken() { return token; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getTenantId() { return tenantId; }
    public String getTenantStatus() { return tenantStatus; }
    public java.util.List<String> getEmpresas() { return empresas; }
    public String getFilialPrincipalId() { return filialPrincipalId; }
    public java.util.List<String> getPermissoes() { return permissoes; }
}
