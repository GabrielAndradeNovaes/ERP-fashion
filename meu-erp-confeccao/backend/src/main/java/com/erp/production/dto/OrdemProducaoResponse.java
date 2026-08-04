package com.erp.production.dto;

import com.erp.production.domain.OrdemProducaoStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrdemProducaoResponse(
        UUID id,
        String numero,
        UUID produtoBaseId,
        String produtoBaseNome,
        UUID fichaTecnicaId,
        String fichaTecnicaVersao,
        Integer quantidade,
        OrdemProducaoStatus status,
        LocalDateTime criadoEm,
        LocalDateTime dataInicio
) {}
