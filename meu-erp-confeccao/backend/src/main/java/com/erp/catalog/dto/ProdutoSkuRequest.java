package com.erp.catalog.dto;

import java.math.BigDecimal;

public record ProdutoSkuRequest(
        String cor,
        String tamanho,
        String codigoBarras,
        BigDecimal precoVenda
) {}
