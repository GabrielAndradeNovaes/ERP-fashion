package com.erp.core.dto;

public record FornecedorRequest(
    String nome,
    String documento,
    String email,
    String telefone,
    String tipo,
    String sigla
) {}
