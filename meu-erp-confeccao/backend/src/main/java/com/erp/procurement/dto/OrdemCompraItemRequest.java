package com.erp.procurement.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrdemCompraItemRequest(
    UUID materialId,
    BigDecimal quantidadeSolicitada,
    BigDecimal precoUnitario
) {}
