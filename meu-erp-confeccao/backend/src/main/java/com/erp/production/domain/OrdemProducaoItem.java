package com.erp.production.domain;

import com.erp.catalog.domain.ProdutoSku;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "ordens_producao_itens")
public class OrdemProducaoItem {

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
    private Integer quantidade;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public OrdemProducao getOrdemProducao() { return ordemProducao; }
    public void setOrdemProducao(OrdemProducao ordemProducao) { this.ordemProducao = ordemProducao; }
    public ProdutoSku getProdutoSku() { return produtoSku; }
    public void setProdutoSku(ProdutoSku produtoSku) { this.produtoSku = produtoSku; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
