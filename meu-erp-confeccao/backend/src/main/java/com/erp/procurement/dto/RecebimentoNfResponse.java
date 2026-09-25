package com.erp.procurement.dto;

import com.erp.procurement.domain.RecebimentoStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RecebimentoNfResponse(
    UUID id,
    String chaveNfe,
    String numeroNf,
    UUID fornecedorId,
    String fornecedorNome,
    UUID ordemCompraId,
    String ordemCompraNumero,
    BigDecimal valorTotalNf,
    RecebimentoStatus status,
    LocalDateTime dataRecebimento,
    LocalDateTime criadoEm,
    List<RecebimentoNfItemResponse> itens
) {}
