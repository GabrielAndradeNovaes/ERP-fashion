package com.erp.inventory.dto;

import java.util.UUID;

public record MaterialResponse(
        UUID id,
        String codigo,
        String nome,
        String descricao,
        String unidadeMedida,
        java.math.BigDecimal custoUnitario
) {}
