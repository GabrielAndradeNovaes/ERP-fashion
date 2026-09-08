package com.erp.production.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutividadeResumo(
    UUID funcionarioId,
    String funcionarioNome,
    long totalCupons,
    BigDecimal tempoPadraoProduzido
) {}
