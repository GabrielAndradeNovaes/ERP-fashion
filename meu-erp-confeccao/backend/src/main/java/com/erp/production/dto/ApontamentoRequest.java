package com.erp.production.dto;

import java.util.UUID;

public record ApontamentoRequest(
        String codigoBarras,
        UUID funcionarioId
) {}
