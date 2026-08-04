package com.erp.core.service;

import com.erp.core.dto.CategoriaRequest;
import com.erp.core.dto.CategoriaResponse;

import java.util.List;
import java.util.UUID;

public interface CategoriaService {
    CategoriaResponse create(CategoriaRequest request);
    List<CategoriaResponse> getAll();
    CategoriaResponse getById(UUID id);
    CategoriaResponse update(UUID id, CategoriaRequest request);
    void delete(UUID id);
}
