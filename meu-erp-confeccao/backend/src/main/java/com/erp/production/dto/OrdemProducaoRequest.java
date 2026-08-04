package com.erp.production.dto;

import java.util.UUID;

public record OrdemProducaoRequest(
        String numero,
        UUID produtoBaseId,
        UUID fichaTecnicaId,
        Integer quantidade
) {}
