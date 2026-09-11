package com.erp.core.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class FaturaSaaSDTO {
    private UUID id;
    private UUID tenantId;
    private String nomeEmpresa;
    private String descricao;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private String status;

    public FaturaSaaSDTO(UUID id, UUID tenantId, String nomeEmpresa, String descricao, BigDecimal valor, LocalDate dataVencimento, String status) {
        this.id = id;
        this.tenantId = tenantId;
        this.nomeEmpresa = nomeEmpresa;
        this.descricao = descricao;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getNomeEmpresa() { return nomeEmpresa; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValor() { return valor; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public String getStatus() { return status; }
}
