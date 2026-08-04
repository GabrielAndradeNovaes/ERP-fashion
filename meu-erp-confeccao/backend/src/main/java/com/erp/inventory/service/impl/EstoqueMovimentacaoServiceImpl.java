package com.erp.inventory.service.impl;

import com.erp.inventory.domain.EstoqueMovimentacao;
import com.erp.inventory.domain.Material;
import com.erp.inventory.domain.TipoMovimentacao;
import com.erp.inventory.repository.EstoqueMovimentacaoRepository;
import com.erp.inventory.repository.MaterialRepository;
import com.erp.inventory.service.EstoqueMovimentacaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class EstoqueMovimentacaoServiceImpl implements EstoqueMovimentacaoService {

    private final EstoqueMovimentacaoRepository movimentacaoRepository;
    private final MaterialRepository materialRepository;

    public EstoqueMovimentacaoServiceImpl(EstoqueMovimentacaoRepository movimentacaoRepository, 
                                          MaterialRepository materialRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.materialRepository = materialRepository;
    }

    @Override
    @Transactional
    public void registrarMovimentacao(UUID materialId, TipoMovimentacao tipo, BigDecimal quantidade, String documentoReferencia) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado."));

        if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }

        EstoqueMovimentacao mov = new EstoqueMovimentacao();
        mov.setMaterial(material);
        mov.setTipo(tipo);
        mov.setQuantidade(quantidade);
        mov.setDocumentoReferencia(documentoReferencia);

        movimentacaoRepository.save(mov);

        // Update Material quantity
        BigDecimal currentQty = material.getQuantidadeAtual() != null ? material.getQuantidadeAtual() : BigDecimal.ZERO;
        if (tipo == TipoMovimentacao.ENTRADA) {
            material.setQuantidadeAtual(currentQty.add(quantidade));
        } else if (tipo == TipoMovimentacao.SAIDA) {
            material.setQuantidadeAtual(currentQty.subtract(quantidade));
        }
        materialRepository.save(material);
    }
}
