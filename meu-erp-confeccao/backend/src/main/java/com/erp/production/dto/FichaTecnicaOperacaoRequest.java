package com.erp.production.dto;

import com.erp.production.domain.DificuldadeTecido;
import com.erp.production.domain.TipoTrajeto;

import java.math.BigDecimal;

public record FichaTecnicaOperacaoRequest(
        String nome,
        String maquina,
        Integer ordemExecucao,
        Integer quantidadeFolhas,
        Integer quantidadeParadas,
        Integer rpmMaquina,
        BigDecimal pontosPorCm,
        BigDecimal comprimentoCosturaCm,
        TipoTrajeto tipoTrajeto,
        DificuldadeTecido dificuldadeTecido
) {
}
