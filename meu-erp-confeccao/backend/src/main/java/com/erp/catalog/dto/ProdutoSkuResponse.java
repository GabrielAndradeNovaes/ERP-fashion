package com.erp.catalog.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoSkuResponse(
        UUID id,
        java.util.UUID corId,
        String corNome,
        java.util.UUID tamanhoId,
        String tamanhoNome,
        String codigoBarras,
        BigDecimal precoVenda,
        Integer quantidadeAtual
) {}
