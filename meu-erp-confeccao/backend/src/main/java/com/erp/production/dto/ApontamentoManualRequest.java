package com.erp.production.dto;

import java.util.UUID;

public record ApontamentoManualRequest(
    UUID funcionarioId,
    Integer minutos,
    String observacao
) {}
