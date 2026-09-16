package com.erp.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TituloRequest(
    String descricao,
    BigDecimal valor,
    LocalDate dataEmissao,
    LocalDate dataVencimento,
    java.util.UUID funcionarioId,
    java.util.UUID clienteId
) {}
