package com.erp.core.domain;

import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "clientes")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
@SQLDelete(sql = "UPDATE clientes SET deleted = true WHERE id=?")
public class Cliente {

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

    @Column(name = "limite_credito", precision = 10, scale = 2)
    private java.math.BigDecimal limiteCredito;

    @Column(name = "tabela_preco_padrao", length = 50)
    private String tabelaPrecoPadrao;

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

    public java.math.BigDecimal getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(java.math.BigDecimal limiteCredito) { this.limiteCredito = limiteCredito; }

    public String getTabelaPrecoPadrao() { return tabelaPrecoPadrao; }
    public void setTabelaPrecoPadrao(String tabelaPrecoPadrao) { this.tabelaPrecoPadrao = tabelaPrecoPadrao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
}
