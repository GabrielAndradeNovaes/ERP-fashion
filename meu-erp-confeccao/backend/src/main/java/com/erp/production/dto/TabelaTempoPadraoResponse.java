package com.erp.production.dto;

import com.erp.production.domain.FaixaComprimentoCostura;
import com.erp.production.domain.GrauDificuldade;
import java.math.BigDecimal;
import java.util.UUID;

public record TabelaTempoPadraoResponse(
        UUID id,
        Integer indice,
        GrauDificuldade grauDificuldade,
        FaixaComprimentoCostura faixaComprimento,
        BigDecimal tempoCentesimal
) {}
