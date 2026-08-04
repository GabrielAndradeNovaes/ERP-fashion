package com.erp.core.service;

import com.erp.core.dto.UnidadeMedidaRequest;
import com.erp.core.dto.UnidadeMedidaResponse;

import java.util.List;
import java.util.UUID;

public interface UnidadeMedidaService {
    UnidadeMedidaResponse create(UnidadeMedidaRequest request);
    List<UnidadeMedidaResponse> getAll();
    UnidadeMedidaResponse getById(UUID id);
    UnidadeMedidaResponse update(UUID id, UnidadeMedidaRequest request);
    void delete(UUID id);
}
