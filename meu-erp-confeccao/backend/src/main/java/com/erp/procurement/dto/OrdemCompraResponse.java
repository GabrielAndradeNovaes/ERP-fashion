package com.erp.procurement.dto;

import com.erp.procurement.domain.OrdemCompraStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrdemCompraResponse(
    UUID id,
    UUID fornecedorId,
    String fornecedorNome,
    String numeroPedido,
    LocalDateTime dataEmissao,
    LocalDate dataPrevisaoEntrega,
    OrdemCompraStatus status,
    BigDecimal valorTotal,
    String observacoes,
    LocalDateTime criadoEm,
    List<OrdemCompraItemResponse> itens
) {}
