package com.erp.inventory.service;

import com.erp.inventory.domain.TipoMovimentacao;
import java.math.BigDecimal;
import java.util.UUID;

public interface EstoqueMovimentacaoService {
    void registrarMovimentacao(UUID materialId, TipoMovimentacao tipo, BigDecimal quantidade, String documentoReferencia);
}
