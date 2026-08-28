package com.erp.inventory.dto;

import java.util.UUID;

public record MaterialResponse(
        UUID id,
        String codigo,
        String nome,
        String descricao,
        String unidadeMedida,
        java.math.BigDecimal custoUnitario,
        java.math.BigDecimal quantidadeAtual,
        String tipoMaterial,
        String composicao,
        String ncm,
        String unidadeCompra,
        java.math.BigDecimal fatorConversao,
        java.math.BigDecimal largura,
        java.math.BigDecimal gramatura,
        java.math.BigDecimal rendimento,
        String status,
        UUID fornecedorPadraoId
) {}
