package com.erp.production.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record FichaTecnicaResponse(
        UUID id,
        UUID produtoBaseId,
        String produtoNome,
        String versao,
        String observacoes,
        BigDecimal tempoPadraoTotalCentesimal,
        BigDecimal custoTotalMateriais,
        List<FichaTecnicaMaterialResponse> materiais,
        List<FichaTecnicaOperacaoResponse> operacoes
) {}
