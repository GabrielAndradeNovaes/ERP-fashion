package com.erp.procurement.domain;

import com.erp.inventory.domain.Material;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "recebimentos_nf_itens")
public class RecebimentoNfItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recebimento_nf_id", nullable = false)
    private RecebimentoNf recebimentoNf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Material material; // Pode ser null se ainda não houver o DE/PARA

    @Column(name = "nome_produto_nf", length = 255)
    private String nomeProdutoNf;

    @Column(name = "codigo_produto_nf", length = 100)
    private String codigoProdutoNf;

    @Column(name = "ncm_nf", length = 20)
    private String ncmNf;

    @Column(nullable = false)
    private BigDecimal quantidade;

    @Column(name = "valor_unitario", nullable = false)
    private BigDecimal valorUnitario;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @PrePersist
    protected void onCreate() {
        calcularValorTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        calcularValorTotal();
    }

    private void calcularValorTotal() {
        if (this.quantidade != null && this.valorUnitario != null) {
            this.valorTotal = this.quantidade.multiply(this.valorUnitario);
        } else {
            this.valorTotal = BigDecimal.ZERO;
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public RecebimentoNf getRecebimentoNf() { return recebimentoNf; }
    public void setRecebimentoNf(RecebimentoNf recebimentoNf) { this.recebimentoNf = recebimentoNf; }
    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }
    public String getNomeProdutoNf() { return nomeProdutoNf; }
    public void setNomeProdutoNf(String nomeProdutoNf) { this.nomeProdutoNf = nomeProdutoNf; }
    public String getCodigoProdutoNf() { return codigoProdutoNf; }
    public void setCodigoProdutoNf(String codigoProdutoNf) { this.codigoProdutoNf = codigoProdutoNf; }
    public String getNcmNf() { return ncmNf; }
    public void setNcmNf(String ncmNf) { this.ncmNf = ncmNf; }
    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}
