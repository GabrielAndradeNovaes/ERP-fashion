package com.erp.core.billing.domain;

import com.erp.core.billing.domain.enums.GatewayPagamento;
import com.erp.core.billing.domain.enums.StatusTransacao;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "faturas_transacoes", schema = "master")
public class TransacaoFatura {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fatura_saas_id", nullable = false)
    private FaturaSaaS faturaSaaS;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GatewayPagamento gateway;

    @Column(name = "gateway_transacao_id")
    private String gatewayTransacaoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacao status;

    @Column(name = "qr_code_payload", columnDefinition = "TEXT")
    private String qrCodePayload;

    @Column(name = "qr_code_image_url", columnDefinition = "TEXT")
    private String qrCodeImageUrl;

    @Column(name = "payload_resposta", columnDefinition = "TEXT")
    private String payloadResposta;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public FaturaSaaS getFaturaSaaS() { return faturaSaaS; }
    public void setFaturaSaaS(FaturaSaaS faturaSaaS) { this.faturaSaaS = faturaSaaS; }

    public GatewayPagamento getGateway() { return gateway; }
    public void setGateway(GatewayPagamento gateway) { this.gateway = gateway; }

    public String getGatewayTransacaoId() { return gatewayTransacaoId; }
    public void setGatewayTransacaoId(String gatewayTransacaoId) { this.gatewayTransacaoId = gatewayTransacaoId; }

    public StatusTransacao getStatus() { return status; }
    public void setStatus(StatusTransacao status) { this.status = status; }

    public String getQrCodePayload() { return qrCodePayload; }
    public void setQrCodePayload(String qrCodePayload) { this.qrCodePayload = qrCodePayload; }

    public String getQrCodeImageUrl() { return qrCodeImageUrl; }
    public void setQrCodeImageUrl(String qrCodeImageUrl) { this.qrCodeImageUrl = qrCodeImageUrl; }

    public String getPayloadResposta() { return payloadResposta; }
    public void setPayloadResposta(String payloadResposta) { this.payloadResposta = payloadResposta; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
