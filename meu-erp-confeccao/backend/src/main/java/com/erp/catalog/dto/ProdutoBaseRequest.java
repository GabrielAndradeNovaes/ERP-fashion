package com.erp.catalog.dto;

import java.util.List;

public record ProdutoBaseRequest(
        String codigo,
        String nome,
        String descricao,
        java.math.BigDecimal precoVenda,
        java.math.BigDecimal precoCusto,
        List<ProdutoSkuRequest> skus
) {}
