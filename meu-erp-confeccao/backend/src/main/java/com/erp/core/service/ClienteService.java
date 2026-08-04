package com.erp.core.service;

import com.erp.core.dto.ClienteRequest;
import com.erp.core.dto.ClienteResponse;

import java.util.List;
import java.util.UUID;

public interface ClienteService {
    ClienteResponse create(ClienteRequest request);
    List<ClienteResponse> getAll();
    ClienteResponse getById(UUID id);
    ClienteResponse update(UUID id, ClienteRequest request);
    void delete(UUID id);
}
