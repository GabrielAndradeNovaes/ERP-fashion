package com.erp.catalog.dto;

import java.util.List;

public record ProdutoBaseRequest(
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
        List<ProdutoSkuRequest> skus
) {}
