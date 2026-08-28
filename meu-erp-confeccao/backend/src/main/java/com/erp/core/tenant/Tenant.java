package com.erp.core.tenant;

import org.hibernate.annotations.Type;

import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clientes_tenant", schema = "master")
public class Tenant {

    @Id
    private UUID id;

    @Column(name = "nome_empresa", nullable = false)
    private String nomeEmpresa;

    @Column(name = "schema_name", nullable = false, unique = true)
    private String schemaName;

    @Column(name = "ativo")
    private Boolean ativo;

    @Column(name = "status")
    private String status;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "cnpj", unique = true)
    private String cnpj;

    @Column(name = "razao_social")
    private String razaoSocial;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(name = "porte")
    private String porte;

    @Column(name = "natureza_juridica")
    private String naturezaJuridica;

    @Column(name = "status_rfb")
    private String statusRfb;

    @Column(name = "data_abertura")
    private LocalDate dataAbertura;

    @Column(name = "email_principal")
    private String emailPrincipal;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "cep")
    private String cep;

    @Column(name = "logradouro")
    private String logradouro;

    @Column(name = "numero")
    private String numero;

    @Column(name = "complemento")
    private String complemento;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "estado")
    private String estado;

    @Column(name = "cnae_principal_codigo")
    private String cnaePrincipalCodigo;

    @Column(name = "cnae_principal_descricao")
    private String cnaePrincipalDescricao;

    @Column(name = "simples_nacional")
    private Boolean simplesNacional;

    @JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "receita_federal_raw_data", columnDefinition = "jsonb")
    private String receitaFederalRawData;

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(String naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public String getStatusRfb() {
        return statusRfb;
    }

    public void setStatusRfb(String statusRfb) {
        this.statusRfb = statusRfb;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getEmailPrincipal() {
        return emailPrincipal;
    }

    public void setEmailPrincipal(String emailPrincipal) {
        this.emailPrincipal = emailPrincipal;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCnaePrincipalCodigo() {
        return cnaePrincipalCodigo;
    }

    public void setCnaePrincipalCodigo(String cnaePrincipalCodigo) {
        this.cnaePrincipalCodigo = cnaePrincipalCodigo;
    }

    public String getCnaePrincipalDescricao() {
        return cnaePrincipalDescricao;
    }

    public void setCnaePrincipalDescricao(String cnaePrincipalDescricao) {
        this.cnaePrincipalDescricao = cnaePrincipalDescricao;
    }

    public Boolean getSimplesNacional() {
        return simplesNacional;
    }

    public void setSimplesNacional(Boolean simplesNacional) {
        this.simplesNacional = simplesNacional;
    }

    public String getReceitaFederalRawData() {
        return receitaFederalRawData;
    }

    public void setReceitaFederalRawData(String receitaFederalRawData) {
        this.receitaFederalRawData = receitaFederalRawData;
    }
}
