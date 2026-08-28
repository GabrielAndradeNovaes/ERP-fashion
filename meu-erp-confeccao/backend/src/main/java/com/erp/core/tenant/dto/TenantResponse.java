package com.erp.core.tenant.dto;

import com.erp.core.tenant.Tenant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TenantResponse {
    private UUID id;
    private String nomeEmpresa;
    private String schemaName;
    private String slug;
    private Boolean ativo;
    private String status;
    private LocalDateTime criadoEm;
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private String porte;
    private String naturezaJuridica;
    private String statusRfb;
    private LocalDate dataAbertura;
    private String emailPrincipal;
    private String telefone;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cnaePrincipalCodigo;
    private String cnaePrincipalDescricao;
    private Boolean simplesNacional;
    
    public TenantResponse(Tenant tenant) {
        this.id = tenant.getId();
        this.nomeEmpresa = tenant.getNomeEmpresa();
        this.schemaName = tenant.getSchemaName();
        this.slug = tenant.getSlug();
        this.ativo = tenant.getAtivo();
        this.status = tenant.getStatus();
        this.criadoEm = tenant.getCriadoEm();
        this.cnpj = tenant.getCnpj();
        this.razaoSocial = tenant.getRazaoSocial();
        this.nomeFantasia = tenant.getNomeFantasia();
        this.porte = tenant.getPorte();
        this.naturezaJuridica = tenant.getNaturezaJuridica();
        this.statusRfb = tenant.getStatusRfb();
        this.dataAbertura = tenant.getDataAbertura();
        this.emailPrincipal = tenant.getEmailPrincipal();
        this.telefone = tenant.getTelefone();
        this.cep = tenant.getCep();
        this.logradouro = tenant.getLogradouro();
        this.numero = tenant.getNumero();
        this.complemento = tenant.getComplemento();
        this.bairro = tenant.getBairro();
        this.cidade = tenant.getCidade();
        this.estado = tenant.getEstado();
        this.cnaePrincipalCodigo = tenant.getCnaePrincipalCodigo();
        this.cnaePrincipalDescricao = tenant.getCnaePrincipalDescricao();
        this.simplesNacional = tenant.getSimplesNacional();
    }

    // Getters
    public UUID getId() { return id; }
    public String getNomeEmpresa() { return nomeEmpresa; }
    public String getSchemaName() { return schemaName; }
    public String getSlug() { return slug; }
    public Boolean getAtivo() { return ativo; }
    public String getStatus() { return status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public String getCnpj() { return cnpj; }
    public String getRazaoSocial() { return razaoSocial; }
    public String getNomeFantasia() { return nomeFantasia; }
    public String getPorte() { return porte; }
    public String getNaturezaJuridica() { return naturezaJuridica; }
    public String getStatusRfb() { return statusRfb; }
    public LocalDate getDataAbertura() { return dataAbertura; }
    public String getEmailPrincipal() { return emailPrincipal; }
    public String getTelefone() { return telefone; }
    public String getCep() { return cep; }
    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getCnaePrincipalCodigo() { return cnaePrincipalCodigo; }
    public String getCnaePrincipalDescricao() { return cnaePrincipalDescricao; }
    public Boolean getSimplesNacional() { return simplesNacional; }
}
