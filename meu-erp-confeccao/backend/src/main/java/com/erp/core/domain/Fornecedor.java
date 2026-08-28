package com.erp.core.domain;

import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.util.UUID;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "fornecedores")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
@SQLDelete(sql = "UPDATE fornecedores SET deleted = true WHERE id=?")
public class Fornecedor {

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;



    @Column(nullable = false)
    private String nome;

    @Column
    private String documento;

    @Column
    private String email;

    @Column
    private String telefone;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(name = "tipo_pessoa", length = 2)
    private String tipoPessoa = "PJ";

    @Column(name = "razao_social", length = 255)
    private String razaoSocial;

    @Column(name = "inscricao_estadual", length = 50)
    private String inscricaoEstadual;

    @Column(name = "categoria_fornecedor", length = 100)
    private String categoriaFornecedor;

    @Column(name = "prazo_pagamento_padrao")
    private Integer prazoPagamentoPadrao;

    @Column(name = "contato_nome", length = 255)
    private String contatoNome;

    @Column(length = 20)
    private String status = "ATIVO";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String endereco;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public String getTipoPessoa() { return tipoPessoa; }
    public void setTipoPessoa(String tipoPessoa) { this.tipoPessoa = tipoPessoa; }

    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }

    public String getInscricaoEstadual() { return inscricaoEstadual; }
    public void setInscricaoEstadual(String inscricaoEstadual) { this.inscricaoEstadual = inscricaoEstadual; }

    public String getCategoriaFornecedor() { return categoriaFornecedor; }
    public void setCategoriaFornecedor(String categoriaFornecedor) { this.categoriaFornecedor = categoriaFornecedor; }

    public Integer getPrazoPagamentoPadrao() { return prazoPagamentoPadrao; }
    public void setPrazoPagamentoPadrao(Integer prazoPagamentoPadrao) { this.prazoPagamentoPadrao = prazoPagamentoPadrao; }

    public String getContatoNome() { return contatoNome; }
    public void setContatoNome(String contatoNome) { this.contatoNome = contatoNome; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
}
