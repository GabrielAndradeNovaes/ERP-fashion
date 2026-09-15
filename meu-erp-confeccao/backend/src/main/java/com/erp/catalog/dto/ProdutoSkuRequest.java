package com.erp.catalog.dto;

import java.math.BigDecimal;

public record ProdutoSkuRequest(
        java.util.UUID corId,
        java.util.UUID tamanhoId,
        String codigoBarras,
        BigDecimal precoVenda
) {}
