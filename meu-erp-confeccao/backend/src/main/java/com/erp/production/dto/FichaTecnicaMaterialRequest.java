package com.erp.production.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FichaTecnicaMaterialRequest(
        UUID materialId,
        BigDecimal quantidade
) {}
