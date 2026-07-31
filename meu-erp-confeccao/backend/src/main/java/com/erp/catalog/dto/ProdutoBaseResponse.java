package com.erp.catalog.dto;

import java.util.List;
import java.util.UUID;

public record ProdutoBaseResponse(
        UUID id,
        String codigo,
        String nome,
        String descricao,
        List<ProdutoSkuResponse> skus
) {}
