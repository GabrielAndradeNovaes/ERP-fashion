package com.erp.production.dto;

import java.util.List;
import java.util.UUID;

public record OrdemProducaoRequest(
        String numero,
        UUID produtoBaseId,
        Integer quantidade,
        List<OrdemProducaoItemRequest> itens
) {}
