package com.erp.catalog.dto;

import java.util.List;
import java.util.UUID;

public record ProdutoBaseResponse(
        UUID id,
        String codigo,
        String nome,
        String descricao,
        java.math.BigDecimal precoVenda,
        java.math.BigDecimal precoCusto,
        String marca,
        String categoria,
        String colecao,
        String genero,
        String ncm,
        String cest,
        String origem,
        java.math.BigDecimal pesoBruto,
        java.math.BigDecimal pesoLiquido,
        String status,
        List<ProdutoSkuResponse> skus,
        com.erp.production.dto.FichaTecnicaResponse fichaTecnica
) {}
