package com.erp.finance.gateway.dto;

import com.erp.finance.domain.enums.StatusTransacao;

public class PaymentResponse {

    private String gatewayTransacaoId;
    private StatusTransacao status;
    private String qrCodePayload;
    private String qrCodeImageUrl;
    private String boletoUrl;
    private String boletoLinhaDigitavel;

    public PaymentResponse() {
    }

    public PaymentResponse(String gatewayTransacaoId, StatusTransacao status) {
        this.gatewayTransacaoId = gatewayTransacaoId;
        this.status = status;
    }

    public String getGatewayTransacaoId() {
        return gatewayTransacaoId;
    }

    public void setGatewayTransacaoId(String gatewayTransacaoId) {
        this.gatewayTransacaoId = gatewayTransacaoId;
    }

    public StatusTransacao getStatus() {
        return status;
    }

    public void setStatus(StatusTransacao status) {
        this.status = status;
    }

    public String getQrCodePayload() {
        return qrCodePayload;
    }

    public void setQrCodePayload(String qrCodePayload) {
        this.qrCodePayload = qrCodePayload;
    }

    public String getQrCodeImageUrl() {
        return qrCodeImageUrl;
    }

    public void setQrCodeImageUrl(String qrCodeImageUrl) {
        this.qrCodeImageUrl = qrCodeImageUrl;
    }

    public String getBoletoUrl() {
        return boletoUrl;
    }

    public void setBoletoUrl(String boletoUrl) {
        this.boletoUrl = boletoUrl;
    }

    public String getBoletoLinhaDigitavel() {
        return boletoLinhaDigitavel;
    }

    public void setBoletoLinhaDigitavel(String boletoLinhaDigitavel) {
        this.boletoLinhaDigitavel = boletoLinhaDigitavel;
    }
}
