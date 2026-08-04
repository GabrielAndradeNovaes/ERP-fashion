package com.erp.core.service;

import com.erp.core.dto.FornecedorRequest;
import com.erp.core.dto.FornecedorResponse;

import java.util.List;
import java.util.UUID;

public interface FornecedorService {
    FornecedorResponse create(FornecedorRequest request);
    List<FornecedorResponse> getAll();
    FornecedorResponse getById(UUID id);
    FornecedorResponse update(UUID id, FornecedorRequest request);
    void delete(UUID id);
}
