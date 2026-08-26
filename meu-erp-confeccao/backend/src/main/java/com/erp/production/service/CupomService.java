package com.erp.production.service;

import com.erp.production.dto.CupomResponse;
import java.util.List;
import java.util.UUID;

public interface CupomService {
    List<CupomResponse> listarPorOrdemProducao(UUID ordemProducaoId);
}
