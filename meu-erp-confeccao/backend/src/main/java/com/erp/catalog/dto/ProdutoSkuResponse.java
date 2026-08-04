package com.erp.catalog.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoSkuResponse(
        UUID id,
        String cor,
        String tamanho,
        String codigoBarras,
        BigDecimal precoVenda,
        Integer quantidadeAtual
) {}
