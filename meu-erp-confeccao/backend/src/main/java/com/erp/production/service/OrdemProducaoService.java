package com.erp.production.service;

import com.erp.production.dto.OrdemProducaoRequest;
import com.erp.production.dto.OrdemProducaoResponse;
import java.util.List;
import java.util.UUID;

public interface OrdemProducaoService {
    OrdemProducaoResponse criarOrdemProducao(OrdemProducaoRequest request);
    List<OrdemProducaoResponse> listarTodas();
    OrdemProducaoResponse iniciarProducao(UUID id);
}
