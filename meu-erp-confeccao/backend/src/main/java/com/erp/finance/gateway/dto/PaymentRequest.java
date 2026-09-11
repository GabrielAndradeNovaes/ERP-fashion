package com.erp.finance.gateway.dto;

import com.erp.finance.domain.enums.MetodoPagamento;
import java.math.BigDecimal;

public class PaymentRequest {

    private String tituloId;
    private BigDecimal valor;
    private String nomeCliente;
    private String emailCliente;
    private String cpfCnpjCliente;
    private MetodoPagamento metodo;
    private String descricao;

    public PaymentRequest() {
    }

    public PaymentRequest(String tituloId, BigDecimal valor, String nomeCliente, String emailCliente, String cpfCnpjCliente, MetodoPagamento metodo, String descricao) {
        this.tituloId = tituloId;
        this.valor = valor;
        this.nomeCliente = nomeCliente;
        this.emailCliente = emailCliente;
        this.cpfCnpjCliente = cpfCnpjCliente;
        this.metodo = metodo;
        this.descricao = descricao;
    }

    public String getTituloId() {
        return tituloId;
    }

    public void setTituloId(String tituloId) {
        this.tituloId = tituloId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public String getCpfCnpjCliente() {
        return cpfCnpjCliente;
    }

    public void setCpfCnpjCliente(String cpfCnpjCliente) {
        this.cpfCnpjCliente = cpfCnpjCliente;
    }

    public MetodoPagamento getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoPagamento metodo) {
        this.metodo = metodo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
