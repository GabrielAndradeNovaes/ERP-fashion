package com.erp.catalog.dto;

import java.util.List;

public record ProdutoBaseRequest(
        String codigo,
        String nome,
        String descricao,
        List<ProdutoSkuRequest> skus
) {}
