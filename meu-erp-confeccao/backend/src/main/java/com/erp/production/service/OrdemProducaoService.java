package com.erp.production.service;

import com.erp.production.dto.OrdemProducaoRequest;
import com.erp.production.dto.OrdemProducaoResponse;
import com.erp.production.domain.OrdemProducaoStatus;
import java.util.List;
import java.util.UUID;

public interface OrdemProducaoService {
    OrdemProducaoResponse criarOrdemProducao(OrdemProducaoRequest request);
    List<OrdemProducaoResponse> listarTodas();
    org.springframework.data.domain.Page<OrdemProducaoResponse> buscarComFiltros(String numero, String produtoId, OrdemProducaoStatus status, java.time.LocalDate dataInicio, java.time.LocalDate dataFim, org.springframework.data.domain.Pageable pageable);
    OrdemProducaoResponse iniciarProducao(UUID id);
    void gerarPacotes(UUID id, int tamanhoPacote);
    void biparPacote(String codigoBarras);
    OrdemProducaoResponse atualizarStatus(UUID id, OrdemProducaoStatus novoStatus);
    OrdemProducaoResponse atualizarOrdemProducao(UUID id, OrdemProducaoRequest request);
    OrdemProducaoResponse estornarOrdemProducao(UUID id);
    void excluirOrdemProducao(UUID id);
}
