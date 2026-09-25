package com.erp.procurement.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RecebimentoNfItemResponse(
    UUID id,
    String produtoNome,
    String produtoCodigo,
    BigDecimal quantidade,
    BigDecimal precoUnitario,
    BigDecimal valorTotal,
    UUID ordemCompraItemId,
    UUID materialId
) {}
