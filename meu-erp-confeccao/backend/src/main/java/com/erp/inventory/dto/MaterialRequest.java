package com.erp.inventory.dto;

public record MaterialRequest(
        String codigo,
        String nome,
        String descricao,
        String unidadeMedida,
        java.math.BigDecimal custoUnitario
) {}
