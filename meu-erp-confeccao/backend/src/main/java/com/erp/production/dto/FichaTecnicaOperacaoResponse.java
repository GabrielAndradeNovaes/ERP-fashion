package com.erp.production.dto;

import com.erp.production.domain.DificuldadeTecido;
import com.erp.production.domain.TipoTrajeto;

import java.math.BigDecimal;
import java.util.UUID;

public record FichaTecnicaOperacaoResponse(
        UUID id,
        String nome,
        String maquina,
        Integer ordemExecucao,
        Integer quantidadeFolhas,
        Integer quantidadeParadas,
        Integer rpmMaquina,
        BigDecimal pontosPorCm,
        BigDecimal comprimentoCosturaCm,
        TipoTrajeto tipoTrajeto,
        DificuldadeTecido dificuldadeTecido,
        BigDecimal samMinutos,
        BigDecimal tempoCalculadoCentesimal
) {
}
