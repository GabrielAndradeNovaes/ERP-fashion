package com.erp.production.service;

import com.erp.production.dto.FichaTecnicaOperacaoRequest;
import com.erp.production.dto.FichaTecnicaRequest;
import com.erp.production.dto.FichaTecnicaResponse;

import java.util.List;
import java.util.UUID;

public interface FichaTecnicaService {
    FichaTecnicaResponse createFichaTecnica(FichaTecnicaRequest request);
    List<FichaTecnicaResponse> getFichasPorProduto(UUID produtoBaseId);
    FichaTecnicaResponse getFichaTecnicaById(UUID id);
    
    FichaTecnicaResponse addOperacao(UUID fichaTecnicaId, FichaTecnicaOperacaoRequest request);
    FichaTecnicaResponse removeOperacao(UUID fichaTecnicaId, UUID operacaoId);
    
    FichaTecnicaResponse addMaterial(UUID fichaTecnicaId, com.erp.production.dto.FichaTecnicaMaterialRequest request);
    FichaTecnicaResponse removeMaterial(UUID fichaTecnicaId, UUID materialId);
}
