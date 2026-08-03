package com.erp.production.dto;

import com.erp.production.domain.FaixaComprimentoCostura;
import com.erp.production.domain.GrauDificuldade;
import java.math.BigDecimal;

public record TabelaTempoPadraoRequest(
        Integer indice,
        GrauDificuldade grauDificuldade,
        FaixaComprimentoCostura faixaComprimento,
        BigDecimal tempoCentesimal
) {}
