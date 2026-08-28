package com.erp.core.tenant.dto;

import java.time.LocalDate;

public class TenantUpdateRequest {
    private String nomeEmpresa;
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
    private String receitaFederalRawData;

    public String getNomeEmpresa() { return nomeEmpresa; }
    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }

    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }

    public String getPorte() { return porte; }
    public void setPorte(String porte) { this.porte = porte; }

    public String getNaturezaJuridica() { return naturezaJuridica; }
    public void setNaturezaJuridica(String naturezaJuridica) { this.naturezaJuridica = naturezaJuridica; }

    public String getStatusRfb() { return statusRfb; }
    public void setStatusRfb(String statusRfb) { this.statusRfb = statusRfb; }

    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate dataAbertura) { this.dataAbertura = dataAbertura; }

    public String getEmailPrincipal() { return emailPrincipal; }
    public void setEmailPrincipal(String emailPrincipal) { this.emailPrincipal = emailPrincipal; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getCnaePrincipalCodigo() { return cnaePrincipalCodigo; }
    public void setCnaePrincipalCodigo(String cnaePrincipalCodigo) { this.cnaePrincipalCodigo = cnaePrincipalCodigo; }

    public String getCnaePrincipalDescricao() { return cnaePrincipalDescricao; }
    public void setCnaePrincipalDescricao(String cnaePrincipalDescricao) { this.cnaePrincipalDescricao = cnaePrincipalDescricao; }

    public Boolean getSimplesNacional() { return simplesNacional; }
    public void setSimplesNacional(Boolean simplesNacional) { this.simplesNacional = simplesNacional; }

    public String getReceitaFederalRawData() { return receitaFederalRawData; }
    public void setReceitaFederalRawData(String receitaFederalRawData) { this.receitaFederalRawData = receitaFederalRawData; }
}
