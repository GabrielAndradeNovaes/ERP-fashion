package com.erp.production.domain;

import com.erp.core.domain.Empresa;
import org.hibernate.annotations.Filter;
import com.erp.catalog.domain.ProdutoSku;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "ordens_producao_itens")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class OrdemProducaoItem {

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }


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
