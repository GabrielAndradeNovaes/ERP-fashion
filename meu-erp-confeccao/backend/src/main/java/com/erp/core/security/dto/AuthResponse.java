package com.erp.core.security.dto;

public class AuthResponse {
    private String token;
    private String refreshToken;
    private String nome;
    private String email;
    private String role;
    private String tenantId;
    private String tenantStatus;
    private java.util.List<String> empresas;
    private String filialPrincipalId;
    private java.util.List<String> permissoes;
    private java.util.List<String> modulosAtivos;

    public AuthResponse(String token, String refreshToken, String nome, String email, String role, String tenantId, String tenantStatus, java.util.List<String> empresas, String filialPrincipalId, java.util.List<String> permissoes, java.util.List<String> modulosAtivos) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.tenantId = tenantId;
        this.tenantStatus = tenantStatus;
        this.empresas = empresas;
        this.filialPrincipalId = filialPrincipalId;
        this.permissoes = permissoes;
        this.modulosAtivos = modulosAtivos;
    }

    public String getToken() { return token; }
    public String getRefreshToken() { return refreshToken; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getTenantId() { return tenantId; }
    public String getTenantStatus() { return tenantStatus; }
    public java.util.List<String> getEmpresas() { return empresas; }
    public String getFilialPrincipalId() { return filialPrincipalId; }
    public java.util.List<String> getPermissoes() { return permissoes; }
    public java.util.List<String> getModulosAtivos() { return modulosAtivos; }
}
