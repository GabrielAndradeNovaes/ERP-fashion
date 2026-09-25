package com.erp.procurement.domain;

import com.erp.inventory.domain.Material;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ordens_compra_itens")
public class OrdemCompraItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ordem_compra_id", nullable = false)
    private OrdemCompra ordemCompra;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(name = "quantidade_solicitada", nullable = false)
    private BigDecimal quantidadeSolicitada;

    @Column(name = "quantidade_recebida")
    private BigDecimal quantidadeRecebida;

    @Column(name = "preco_unitario", nullable = false)
    private BigDecimal precoUnitario;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @PrePersist
    protected void onCreate() {
        if (this.quantidadeRecebida == null) {
            this.quantidadeRecebida = BigDecimal.ZERO;
        }
        calcularValorTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        calcularValorTotal();
    }

    private void calcularValorTotal() {
        if (this.quantidadeSolicitada != null && this.precoUnitario != null) {
            this.valorTotal = this.quantidadeSolicitada.multiply(this.precoUnitario);
        } else {
            this.valorTotal = BigDecimal.ZERO;
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public OrdemCompra getOrdemCompra() { return ordemCompra; }
    public void setOrdemCompra(OrdemCompra ordemCompra) { this.ordemCompra = ordemCompra; }
    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }
    public BigDecimal getQuantidadeSolicitada() { return quantidadeSolicitada; }
    public void setQuantidadeSolicitada(BigDecimal quantidadeSolicitada) { this.quantidadeSolicitada = quantidadeSolicitada; }
    public BigDecimal getQuantidadeRecebida() { return quantidadeRecebida; }
    public void setQuantidadeRecebida(BigDecimal quantidadeRecebida) { this.quantidadeRecebida = quantidadeRecebida; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
}
