package com.erp.inventory.domain;

import com.erp.core.domain.Empresa;
import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "materiais")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class Material {

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "unidade_medida", nullable = false, length = 20)
    private String unidadeMedida; // Ex: KG, METRO, UNIDADE

    @Column(name = "custo_unitario", precision = 10, scale = 2)
    private BigDecimal custoUnitario;

    @Column(name = "quantidade_atual", precision = 19, scale = 4)
    private BigDecimal quantidadeAtual = BigDecimal.ZERO;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "tipo_material", length = 50)
    private String tipoMaterial;

    @Column(length = 255)
    private String composicao;

    @Column(length = 20)
    private String ncm;

    @Column(name = "unidade_compra", length = 20)
    private String unidadeCompra;

    @Column(name = "fator_conversao", precision = 10, scale = 4)
    private BigDecimal fatorConversao;

    @Column(precision = 10, scale = 2)
    private BigDecimal largura;

    @Column(precision = 10, scale = 2)
    private BigDecimal gramatura;

    @Column(precision = 10, scale = 2)
    private BigDecimal rendimento;

    @Column(length = 20)
    private String status = "ATIVO";

    @Column(name = "fornecedor_padrao_id")
    private UUID fornecedorPadraoId;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getCustoUnitario() {
        return custoUnitario;
    }

    public void setCustoUnitario(BigDecimal custoUnitario) {
        this.custoUnitario = custoUnitario;
    }

    public BigDecimal getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public void setQuantidadeAtual(BigDecimal quantidadeAtual) {
        this.quantidadeAtual = quantidadeAtual;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getTipoMaterial() { return tipoMaterial; }
    public void setTipoMaterial(String tipoMaterial) { this.tipoMaterial = tipoMaterial; }

    public String getComposicao() { return composicao; }
    public void setComposicao(String composicao) { this.composicao = composicao; }

    public String getNcm() { return ncm; }
    public void setNcm(String ncm) { this.ncm = ncm; }

    public String getUnidadeCompra() { return unidadeCompra; }
    public void setUnidadeCompra(String unidadeCompra) { this.unidadeCompra = unidadeCompra; }

    public BigDecimal getFatorConversao() { return fatorConversao; }
    public void setFatorConversao(BigDecimal fatorConversao) { this.fatorConversao = fatorConversao; }

    public BigDecimal getLargura() { return largura; }
    public void setLargura(BigDecimal largura) { this.largura = largura; }

    public BigDecimal getGramatura() { return gramatura; }
    public void setGramatura(BigDecimal gramatura) { this.gramatura = gramatura; }

    public BigDecimal getRendimento() { return rendimento; }
    public void setRendimento(BigDecimal rendimento) { this.rendimento = rendimento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UUID getFornecedorPadraoId() { return fornecedorPadraoId; }
    public void setFornecedorPadraoId(UUID fornecedorPadraoId) { this.fornecedorPadraoId = fornecedorPadraoId; }
}
