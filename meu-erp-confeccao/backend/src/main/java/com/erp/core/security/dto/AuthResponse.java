package com.erp.core.security.dto;

public class AuthResponse {
    private String token;
    private String nome;
    private String email;
    private String role;
    private String tenantId;

    public AuthResponse(String token, String nome, String email, String role, String tenantId) {
        this.token = token;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.tenantId = tenantId;
    }

    public String getToken() { return token; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getTenantId() { return tenantId; }
}
