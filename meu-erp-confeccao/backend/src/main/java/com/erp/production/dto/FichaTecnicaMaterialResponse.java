package com.erp.production.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FichaTecnicaMaterialResponse(
        UUID id,
        UUID materialId,
        String materialNome,
        String materialUnidadeMedida,
        BigDecimal quantidade
) {}
