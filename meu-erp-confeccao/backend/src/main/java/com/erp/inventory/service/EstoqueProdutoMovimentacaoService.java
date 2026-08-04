package com.erp.inventory.service;

import com.erp.inventory.domain.EstoqueProdutoMovimentacao;
import com.erp.inventory.domain.TipoMovimentacao;

import java.util.List;
import java.util.UUID;

public interface EstoqueProdutoMovimentacaoService {
    EstoqueProdutoMovimentacao registrarMovimentacao(UUID skuId, TipoMovimentacao tipo, Integer quantidade, String documentoReferencia);
    List<EstoqueProdutoMovimentacao> listarHistoricoPorSku(UUID skuId);
}
