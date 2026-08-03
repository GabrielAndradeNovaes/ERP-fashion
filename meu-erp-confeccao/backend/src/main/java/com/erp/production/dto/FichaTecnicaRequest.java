package com.erp.production.dto;

import java.util.List;
import java.util.UUID;

public record FichaTecnicaRequest(
        UUID produtoBaseId,
        String versao,
        String observacoes,
        List<FichaTecnicaMaterialRequest> materiais
) {}
