package com.erp.production.dto;

import java.util.UUID;
import java.math.BigDecimal;

public record OcorrenciaRequest(
        UUID funcionarioId,
        String motivo,
        BigDecimal tempoDescontadoCentesimal
) {}
