package com.erp.production.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutividadeResumo(
    UUID funcionarioId,
    String funcionarioNome,
    long totalCupons,
    BigDecimal tempoPadraoProduzido,
    BigDecimal metaMinima,
    BigDecimal premio100,
    Integer tempoTeorico
) {
    public ProdutividadeResumo(UUID funcionarioId, String funcionarioNome, long totalCupons, BigDecimal tempoPadraoProduzido) {
        this(funcionarioId, funcionarioNome, totalCupons, tempoPadraoProduzido, null, null, null);
    }
}
