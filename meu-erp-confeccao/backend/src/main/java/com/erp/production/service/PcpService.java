package com.erp.production.service;

import com.erp.production.dto.ApontamentoRequest;
import com.erp.production.dto.OcorrenciaRequest;
import com.erp.production.dto.ProdutividadeResponse;

import java.util.UUID;

public interface PcpService {
    void biparCupom(ApontamentoRequest request);
    void registrarOcorrencia(OcorrenciaRequest request);
    ProdutividadeResponse calcularProdutividade(UUID funcionarioId, int ano, int mes);
}
