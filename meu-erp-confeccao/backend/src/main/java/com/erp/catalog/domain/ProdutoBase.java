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

    @Column(length = 100)
    private String marca;

    @Column(length = 100)
    private String categoria;

    @Column(length = 100)
    private String colecao;

    @Column(length = 20)
    private String genero;

    @Column(length = 20)
    private String ncm;

    @Column(length = 20)
    private String cest;

    @Column(length = 50)
    private String origem;

    @Column(name = "peso_bruto", precision = 10, scale = 3)
    private java.math.BigDecimal pesoBruto;

    @Column(name = "peso_liquido", precision = 10, scale = 3)
    private java.math.BigDecimal pesoLiquido;

    @Column(length = 20)
    private String status = "ATIVO";

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

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getColecao() { return colecao; }
    public void setColecao(String colecao) { this.colecao = colecao; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getNcm() { return ncm; }
    public void setNcm(String ncm) { this.ncm = ncm; }

    public String getCest() { return cest; }
    public void setCest(String cest) { this.cest = cest; }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }

    public java.math.BigDecimal getPesoBruto() { return pesoBruto; }
    public void setPesoBruto(java.math.BigDecimal pesoBruto) { this.pesoBruto = pesoBruto; }

    public java.math.BigDecimal getPesoLiquido() { return pesoLiquido; }
    public void setPesoLiquido(java.math.BigDecimal pesoLiquido) { this.pesoLiquido = pesoLiquido; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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
