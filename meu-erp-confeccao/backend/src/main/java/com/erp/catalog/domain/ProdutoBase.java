package com.erp.catalog.domain;

import com.erp.core.domain.Empresa;
import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "produtos_base")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class ProdutoBase {

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

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "preco_venda", precision = 10, scale = 2)
    private java.math.BigDecimal precoVenda;

    @Column(name = "preco_custo", precision = 10, scale = 2)
    private java.math.BigDecimal precoCusto;

    @OneToMany(mappedBy = "produtoBase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProdutoSku> skus = new ArrayList<>();

    @OneToOne(mappedBy = "produtoBase", cascade = CascadeType.ALL, orphanRemoval = true)
    private com.erp.production.domain.FichaTecnica fichaTecnica;

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

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public java.math.BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(java.math.BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public java.math.BigDecimal getPrecoCusto() {
        return precoCusto;
    }

    public void setPrecoCusto(java.math.BigDecimal precoCusto) {
        this.precoCusto = precoCusto;
    }

    public List<ProdutoSku> getSkus() {
        return skus;
    }

    public void setSkus(List<ProdutoSku> skus) {
        this.skus = skus;
    }

    public void addSku(ProdutoSku sku) {
        skus.add(sku);
        sku.setProdutoBase(this);
    }

    public void removeSku(ProdutoSku sku) {
        skus.remove(sku);
        sku.setProdutoBase(null);
    }

    public com.erp.production.domain.FichaTecnica getFichaTecnica() {
        return fichaTecnica;
    }

    public void setFichaTecnica(com.erp.production.domain.FichaTecnica fichaTecnica) {
        this.fichaTecnica = fichaTecnica;
        if (fichaTecnica != null) {
            fichaTecnica.setProdutoBase(this);
        }
    }
}
