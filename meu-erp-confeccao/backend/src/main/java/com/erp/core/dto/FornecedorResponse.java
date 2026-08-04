package com.erp.core.dto;

import java.util.UUID;

public record FornecedorResponse(
    UUID id,
    String nome,
    String documento,
    String email,
    String telefone,
    String tipo,
    String sigla
) {}
