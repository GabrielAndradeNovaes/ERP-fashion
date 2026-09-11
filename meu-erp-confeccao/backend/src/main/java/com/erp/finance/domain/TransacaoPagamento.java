package com.erp.finance.domain;

import com.erp.finance.domain.enums.GatewayPagamento;
import com.erp.finance.domain.enums.MetodoPagamento;
import com.erp.finance.domain.enums.StatusTransacao;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "financeiro_transacoes_pagamento")
public class TransacaoPagamento {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titulo_id", nullable = false)
    private TituloReceber titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GatewayPagamento gateway;

    @Column(name = "gateway_transacao_id")
    private String gatewayTransacaoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacao status;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = false)
    private MetodoPagamento metodoPagamento;

    @Column(name = "qr_code_payload", columnDefinition = "TEXT")
    private String qrCodePayload;

    @Column(name = "qr_code_image_url", columnDefinition = "TEXT")
    private String qrCodeImageUrl;

    @Column(name = "boleto_url", columnDefinition = "TEXT")
    private String boletoUrl;

    @Column(name = "boleto_linha_digitavel", columnDefinition = "TEXT")
    private String boletoLinhaDigitavel;

    @Column(name = "webhook_payload", columnDefinition = "JSONB")
    private String webhookPayload;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        if (criadoEm == null) criadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public TituloReceber getTitulo() { return titulo; }
    public void setTitulo(TituloReceber titulo) { this.titulo = titulo; }
    public GatewayPagamento getGateway() { return gateway; }
    public void setGateway(GatewayPagamento gateway) { this.gateway = gateway; }
    public String getGatewayTransacaoId() { return gatewayTransacaoId; }
    public void setGatewayTransacaoId(String gatewayTransacaoId) { this.gatewayTransacaoId = gatewayTransacaoId; }
    public StatusTransacao getStatus() { return status; }
    public void setStatus(StatusTransacao status) { this.status = status; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    public String getQrCodePayload() { return qrCodePayload; }
    public void setQrCodePayload(String qrCodePayload) { this.qrCodePayload = qrCodePayload; }
    public String getQrCodeImageUrl() { return qrCodeImageUrl; }
    public void setQrCodeImageUrl(String qrCodeImageUrl) { this.qrCodeImageUrl = qrCodeImageUrl; }
    public String getBoletoUrl() { return boletoUrl; }
    public void setBoletoUrl(String boletoUrl) { this.boletoUrl = boletoUrl; }
    public String getBoletoLinhaDigitavel() { return boletoLinhaDigitavel; }
    public void setBoletoLinhaDigitavel(String boletoLinhaDigitavel) { this.boletoLinhaDigitavel = boletoLinhaDigitavel; }
    public String getWebhookPayload() { return webhookPayload; }
    public void setWebhookPayload(String webhookPayload) { this.webhookPayload = webhookPayload; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
