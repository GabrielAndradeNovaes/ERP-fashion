package com.erp.finance.dto;

import com.erp.finance.domain.enums.StatusTransacao;
import java.math.BigDecimal;

public class CheckoutResponseDTO {
    private String tituloDescricao;
    private BigDecimal valor;
    private String statusTitulo;
    private String qrCodePayload;
    private String qrCodeImageUrl;
    private StatusTransacao statusTransacao;

    public CheckoutResponseDTO() {}

    public CheckoutResponseDTO(String tituloDescricao, BigDecimal valor, String statusTitulo, String qrCodePayload, String qrCodeImageUrl, StatusTransacao statusTransacao) {
        this.tituloDescricao = tituloDescricao;
        this.valor = valor;
        this.statusTitulo = statusTitulo;
        this.qrCodePayload = qrCodePayload;
        this.qrCodeImageUrl = qrCodeImageUrl;
        this.statusTransacao = statusTransacao;
    }

    public String getTituloDescricao() { return tituloDescricao; }
    public void setTituloDescricao(String tituloDescricao) { this.tituloDescricao = tituloDescricao; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getStatusTitulo() { return statusTitulo; }
    public void setStatusTitulo(String statusTitulo) { this.statusTitulo = statusTitulo; }
    public String getQrCodePayload() { return qrCodePayload; }
    public void setQrCodePayload(String qrCodePayload) { this.qrCodePayload = qrCodePayload; }
    public String getQrCodeImageUrl() { return qrCodeImageUrl; }
    public void setQrCodeImageUrl(String qrCodeImageUrl) { this.qrCodeImageUrl = qrCodeImageUrl; }
    public StatusTransacao getStatusTransacao() { return statusTransacao; }
    public void setStatusTransacao(StatusTransacao statusTransacao) { this.statusTransacao = statusTransacao; }
}
