package com.erp.core.billing.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CheckoutResponseDTO {
    private UUID faturaId;
    private String nomeEmpresa;
    private String descricao;
    private BigDecimal valor;
    private String status;
    private String qrCodePayload;
    private String gatewayTransacaoId;

    public CheckoutResponseDTO(UUID faturaId, String nomeEmpresa, String descricao, BigDecimal valor, String status, String qrCodePayload, String gatewayTransacaoId) {
        this.faturaId = faturaId;
        this.nomeEmpresa = nomeEmpresa;
        this.descricao = descricao;
        this.valor = valor;
        this.status = status;
        this.qrCodePayload = qrCodePayload;
        this.gatewayTransacaoId = gatewayTransacaoId;
    }

    public UUID getFaturaId() { return faturaId; }
    public String getNomeEmpresa() { return nomeEmpresa; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValor() { return valor; }
    public String getStatus() { return status; }
    public String getQrCodePayload() { return qrCodePayload; }
    public String getGatewayTransacaoId() { return gatewayTransacaoId; }
}
