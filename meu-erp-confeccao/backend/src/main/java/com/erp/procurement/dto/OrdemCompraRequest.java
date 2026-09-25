package com.erp.procurement.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OrdemCompraRequest(
    UUID fornecedorId,
    String numeroPedido,
    LocalDate dataPrevisaoEntrega,
    String observacoes,
    List<OrdemCompraItemRequest> itens
) {}
