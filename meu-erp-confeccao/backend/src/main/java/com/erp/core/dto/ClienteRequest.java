package com.erp.core.dto;

public record ClienteRequest(
    String nome,
    String documento,
    String email,
    String telefone,
    String tipoPessoa,
    String razaoSocial,
    String inscricaoEstadual,
    java.math.BigDecimal limiteCredito,
    String tabelaPrecoPadrao,
    String status,
    String endereco
) {}
