package com.erp.core.dto;

public record ClienteRequest(
    String nome,
    String documento,
    String email,
    String telefone,
    String tipo,
    String sigla
) {}
