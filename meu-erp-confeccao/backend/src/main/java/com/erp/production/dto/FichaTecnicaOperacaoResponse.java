package com.erp.production.dto;

import com.erp.production.domain.FaixaComprimentoCostura;
import com.erp.production.domain.GrauDificuldade;
import java.math.BigDecimal;
import java.util.UUID;

public record FichaTecnicaOperacaoResponse(
        UUID id,
        String nome,
        String maquina,
        Integer ordemExecucao,
        Integer quantidadeFolhas,
        Integer quantidadeParadas,
        GrauDificuldade grauDificuldade,
        FaixaComprimentoCostura faixaComprimento,
        BigDecimal tempoCalculadoCentesimal
) {}
