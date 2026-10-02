package com.erp.inventory.domain;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Fornecedor;
import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "materiais_fornecedor",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"fornecedor_id", "codigo_fornecedor"})
    }
)
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class MaterialFornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(name = "codigo_fornecedor", nullable = false, length = 100)
    private String codigoFornecedor;

    @Column(name = "descricao_fornecedor", length = 255)
    private String descricaoFornecedor;

    @Column(name = "fator_conversao", precision = 10, scale = 4)
    private BigDecimal fatorConversao = BigDecimal.ONE;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getCodigoFornecedor() {
        return codigoFornecedor;
    }

    public void setCodigoFornecedor(String codigoFornecedor) {
        this.codigoFornecedor = codigoFornecedor;
    }

    public String getDescricaoFornecedor() {
        return descricaoFornecedor;
    }

    public void setDescricaoFornecedor(String descricaoFornecedor) {
        this.descricaoFornecedor = descricaoFornecedor;
    }

    public BigDecimal getFatorConversao() {
        return fatorConversao;
    }

    public void setFatorConversao(BigDecimal fatorConversao) {
        this.fatorConversao = fatorConversao;
    }
}
