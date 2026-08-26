package com.erp.production.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutividadeResponse(
        UUID funcionarioId,
        String funcionarioNome,
        int mes,
        int ano,
        BigDecimal tempoProduzidoCentesimal,
        BigDecimal cargaHorariaMensal,
        BigDecimal tempoOcorrenciasCentesimal,
        BigDecimal eficienciaPercentual
) {}
