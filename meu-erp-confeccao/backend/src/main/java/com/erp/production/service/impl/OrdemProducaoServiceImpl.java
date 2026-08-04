package com.erp.production.service.impl;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.inventory.domain.TipoMovimentacao;
import com.erp.inventory.service.EstoqueMovimentacaoService;
import com.erp.production.domain.FichaTecnica;
import com.erp.production.domain.OrdemProducao;
import com.erp.production.domain.OrdemProducaoStatus;
import com.erp.production.dto.OrdemProducaoRequest;
import com.erp.production.dto.OrdemProducaoResponse;
import com.erp.production.repository.FichaTecnicaRepository;
import com.erp.production.repository.OrdemProducaoRepository;
import com.erp.production.service.OrdemProducaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrdemProducaoServiceImpl implements OrdemProducaoService {

    private final OrdemProducaoRepository ordemProducaoRepository;
    private final ProdutoBaseRepository produtoBaseRepository;
    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final EstoqueMovimentacaoService estoqueMovimentacaoService;

    public OrdemProducaoServiceImpl(OrdemProducaoRepository ordemProducaoRepository,
                                    ProdutoBaseRepository produtoBaseRepository,
                                    FichaTecnicaRepository fichaTecnicaRepository,
                                    EstoqueMovimentacaoService estoqueMovimentacaoService) {
        this.ordemProducaoRepository = ordemProducaoRepository;
        this.produtoBaseRepository = produtoBaseRepository;
        this.fichaTecnicaRepository = fichaTecnicaRepository;
        this.estoqueMovimentacaoService = estoqueMovimentacaoService;
    }

    @Override
    @Transactional
    public OrdemProducaoResponse criarOrdemProducao(OrdemProducaoRequest request) {
        if (ordemProducaoRepository.existsByNumero(request.numero())) {
            throw new IllegalArgumentException("Ordem de Produção com número " + request.numero() + " já existe.");
        }

        ProdutoBase produto = produtoBaseRepository.findById(request.produtoBaseId())
                .orElseThrow(() -> new IllegalArgumentException("Produto base não encontrado."));

        FichaTecnica ficha = fichaTecnicaRepository.findById(request.fichaTecnicaId())
                .orElseThrow(() -> new IllegalArgumentException("Ficha técnica não encontrada."));

        OrdemProducao op = new OrdemProducao();
        op.setNumero(request.numero());
        op.setProdutoBase(produto);
        op.setFichaTecnica(ficha);
        op.setQuantidade(request.quantidade());
        op.setStatus(OrdemProducaoStatus.CADASTRADA);

        OrdemProducao saved = ordemProducaoRepository.save(op);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemProducaoResponse> listarTodas() {
        return ordemProducaoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrdemProducaoResponse iniciarProducao(UUID id) {
        OrdemProducao op = ordemProducaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de Produção não encontrada."));

        if (op.getStatus() != OrdemProducaoStatus.CADASTRADA) {
            throw new IllegalStateException("Ordem de Produção deve estar no status CADASTRADA para ser iniciada.");
        }

        // Explosão de materiais (BOM)
        FichaTecnica ficha = op.getFichaTecnica();
        BigDecimal qtdOp = new BigDecimal(op.getQuantidade());

        ficha.getMateriais().forEach(fm -> {
            BigDecimal consumoTotal = fm.getQuantidade().multiply(qtdOp);
            // Registra a saída no estoque
            estoqueMovimentacaoService.registrarMovimentacao(
                    fm.getMaterial().getId(),
                    TipoMovimentacao.SAIDA,
                    consumoTotal,
                    "OP: " + op.getNumero()
            );
        });

        op.setStatus(OrdemProducaoStatus.EM_ANDAMENTO);
        op.setDataInicio(LocalDateTime.now());
        
        OrdemProducao saved = ordemProducaoRepository.save(op);
        return mapToResponse(saved);
    }

    private OrdemProducaoResponse mapToResponse(OrdemProducao op) {
        return new OrdemProducaoResponse(
                op.getId(),
                op.getNumero(),
                op.getProdutoBase().getId(),
                op.getProdutoBase().getNome(),
                op.getFichaTecnica().getId(),
                op.getFichaTecnica().getVersao(),
                op.getQuantidade(),
                op.getStatus(),
                op.getCriadoEm(),
                op.getDataInicio()
        );
    }
}
