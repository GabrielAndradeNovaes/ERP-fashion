package com.erp.production.domain;

import com.erp.catalog.domain.ProdutoSku;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "pacotes")
public class Pacote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ordem_producao_id", nullable = false)
    private OrdemProducao ordemProducao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_sku_id", nullable = false)
    private ProdutoSku produtoSku;

    @Column(nullable = false)
    private Integer sequencial;

    @Column(name = "quantidade_pecas", nullable = false)
    private Integer quantidadePecas;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public OrdemProducao getOrdemProducao() { return ordemProducao; }
    public void setOrdemProducao(OrdemProducao ordemProducao) { this.ordemProducao = ordemProducao; }
    public ProdutoSku getProdutoSku() { return produtoSku; }
    public void setProdutoSku(ProdutoSku produtoSku) { this.produtoSku = produtoSku; }
    public Integer getSequencial() { return sequencial; }
    public void setSequencial(Integer sequencial) { this.sequencial = sequencial; }
    public Integer getQuantidadePecas() { return quantidadePecas; }
    public void setQuantidadePecas(Integer quantidadePecas) { this.quantidadePecas = quantidadePecas; }
}
