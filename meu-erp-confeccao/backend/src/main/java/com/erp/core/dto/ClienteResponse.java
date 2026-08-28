package com.erp.core.dto;

import java.util.UUID;

public record ClienteResponse(
    UUID id,
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
