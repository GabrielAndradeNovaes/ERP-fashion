package com.erp.core.tenant.dto;

public class TenantProvisionRequest {
    private String nomeEmpresa;
    private String schemaName;
    private String adminNome;
    private String adminEmail;
    private String adminSenha;

    // Getters and Setters

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getAdminNome() {
        return adminNome;
    }

    public void setAdminNome(String adminNome) {
        this.adminNome = adminNome;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminSenha() {
        return adminSenha;
    }

    public void setAdminSenha(String adminSenha) {
        this.adminSenha = adminSenha;
    }
}
