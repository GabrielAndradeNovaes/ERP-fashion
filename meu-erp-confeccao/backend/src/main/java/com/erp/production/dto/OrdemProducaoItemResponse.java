package com.erp.production.dto;

import java.util.UUID;

public record OrdemProducaoItemResponse(
        UUID id,
        UUID produtoSkuId,
        String produtoSkuCodigoBarras,
        String produtoSkuCor,
        String produtoSkuTamanho,
        Integer quantidade
) {}
