package com.erp.inventory.dto;

public record MaterialRequest(
        String codigo,
        String nome,
        String descricao,
        String unidadeMedida,
        java.math.BigDecimal custoUnitario,
        String tipoMaterial,
        String composicao,
        String ncm,
        String unidadeCompra,
        java.math.BigDecimal fatorConversao,
        java.math.BigDecimal largura,
        java.math.BigDecimal gramatura,
        java.math.BigDecimal rendimento,
        String status,
        java.util.UUID fornecedorPadraoId
) {}
