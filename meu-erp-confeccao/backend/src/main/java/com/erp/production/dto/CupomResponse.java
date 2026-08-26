package com.erp.production.dto;

import com.erp.production.domain.Cupom.Status;
import java.math.BigDecimal;
import java.util.UUID;

public record CupomResponse(
        UUID id,
        String ordemProducaoNumero,
        Integer pacoteSequencial,
        String operacaoNome,
        String codigoBarras,
        BigDecimal tempoTotalCentesimal,
        Integer quantidadePecas,
        Status status
) {}
