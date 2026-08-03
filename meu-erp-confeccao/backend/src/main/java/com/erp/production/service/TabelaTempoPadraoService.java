package com.erp.production.service;

import com.erp.production.dto.TabelaTempoPadraoRequest;
import com.erp.production.dto.TabelaTempoPadraoResponse;

import java.util.List;
import java.util.UUID;

public interface TabelaTempoPadraoService {
    TabelaTempoPadraoResponse create(TabelaTempoPadraoRequest request);
    TabelaTempoPadraoResponse update(UUID id, TabelaTempoPadraoRequest request);
    List<TabelaTempoPadraoResponse> getAll();
    void delete(UUID id);
}
