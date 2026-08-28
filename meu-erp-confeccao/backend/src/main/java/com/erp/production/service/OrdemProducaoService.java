package com.erp.production.service;

import com.erp.production.dto.OrdemProducaoRequest;
import com.erp.production.dto.OrdemProducaoResponse;
import com.erp.production.domain.OrdemProducaoStatus;
import java.util.List;
import java.util.UUID;

public interface OrdemProducaoService {
    OrdemProducaoResponse criarOrdemProducao(OrdemProducaoRequest request);
    List<OrdemProducaoResponse> listarTodas();
    OrdemProducaoResponse iniciarProducao(UUID id);
    void gerarPacotes(UUID id, int tamanhoPacote);
    OrdemProducaoResponse atualizarStatus(UUID id, OrdemProducaoStatus novoStatus);
    OrdemProducaoResponse atualizarOrdemProducao(UUID id, OrdemProducaoRequest request);
    OrdemProducaoResponse estornarOrdemProducao(UUID id);
}
