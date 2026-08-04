package com.erp.inventory.service.impl;

import com.erp.catalog.domain.ProdutoSku;
import com.erp.catalog.repository.ProdutoSkuRepository;
import com.erp.inventory.domain.EstoqueProdutoMovimentacao;
import com.erp.inventory.domain.TipoMovimentacao;
import com.erp.inventory.repository.EstoqueProdutoMovimentacaoRepository;
import com.erp.inventory.service.EstoqueProdutoMovimentacaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EstoqueProdutoMovimentacaoServiceImpl implements EstoqueProdutoMovimentacaoService {

    private final EstoqueProdutoMovimentacaoRepository movimentacaoRepository;
    private final ProdutoSkuRepository skuRepository;

    public EstoqueProdutoMovimentacaoServiceImpl(EstoqueProdutoMovimentacaoRepository movimentacaoRepository,
                                                 ProdutoSkuRepository skuRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.skuRepository = skuRepository;
    }

    @Override
    @Transactional
    public EstoqueProdutoMovimentacao registrarMovimentacao(UUID skuId, TipoMovimentacao tipo, Integer quantidade, String documentoReferencia) {
        ProdutoSku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new IllegalArgumentException("SKU não encontrado com ID: " + skuId));

        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade da movimentação deve ser maior que zero.");
        }

        if (tipo == TipoMovimentacao.ENTRADA) {
            sku.setQuantidadeAtual(sku.getQuantidadeAtual() + quantidade);
        } else if (tipo == TipoMovimentacao.SAIDA) {
            // Permitindo saldo negativo conforme ADR 002 (simplificado)
            sku.setQuantidadeAtual(sku.getQuantidadeAtual() - quantidade);
        }

        skuRepository.save(sku);

        EstoqueProdutoMovimentacao mov = new EstoqueProdutoMovimentacao();
        mov.setSku(sku);
        mov.setTipo(tipo);
        mov.setQuantidade(quantidade);
        mov.setDocumentoReferencia(documentoReferencia);

        return movimentacaoRepository.save(mov);
    }

    @Override
    public List<EstoqueProdutoMovimentacao> listarHistoricoPorSku(UUID skuId) {
        return movimentacaoRepository.findBySkuIdOrderByDataMovimentacaoDesc(skuId);
    }
}
