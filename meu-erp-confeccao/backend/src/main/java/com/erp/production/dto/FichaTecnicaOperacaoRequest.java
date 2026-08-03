package com.erp.production.dto;

import com.erp.production.domain.FaixaComprimentoCostura;
import com.erp.production.domain.GrauDificuldade;

public record FichaTecnicaOperacaoRequest(
        String nome,
        String maquina,
        Integer ordemExecucao,
        Integer quantidadeFolhas,
        Integer quantidadeParadas,
        GrauDificuldade grauDificuldade,
        FaixaComprimentoCostura faixaComprimento
) {}
