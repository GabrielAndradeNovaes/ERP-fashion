package com.erp.production.dto;

import java.util.UUID;

public record OrdemProducaoItemRequest(
        UUID produtoSkuId,
        Integer quantidade
) {}
