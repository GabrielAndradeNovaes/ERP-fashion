package com.erp.procurement.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrdemCompraItemResponse(
    UUID id,
    UUID materialId,
    String materialNome,
    BigDecimal quantidadeSolicitada,
    BigDecimal quantidadeRecebida,
    BigDecimal precoUnitario,
    BigDecimal valorTotal
) {}
