package com.erp.production.service.impl;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.domain.ProdutoSku;
import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.catalog.repository.ProdutoSkuRepository;
import com.erp.inventory.domain.TipoMovimentacao;
import com.erp.inventory.service.EstoqueMovimentacaoService;
import com.erp.production.domain.*;
import com.erp.production.dto.OrdemProducaoRequest;
import com.erp.production.dto.OrdemProducaoResponse;
import com.erp.production.dto.OrdemProducaoItemResponse;
import com.erp.production.repository.*;
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
    private final ProdutoSkuRepository produtoSkuRepository;
    private final PacoteRepository pacoteRepository;
    private final CupomRepository cupomRepository;

    public OrdemProducaoServiceImpl(OrdemProducaoRepository ordemProducaoRepository,
                                    ProdutoBaseRepository produtoBaseRepository,
                                    FichaTecnicaRepository fichaTecnicaRepository,
                                    EstoqueMovimentacaoService estoqueMovimentacaoService,
                                    ProdutoSkuRepository produtoSkuRepository,
                                    PacoteRepository pacoteRepository,
                                    CupomRepository cupomRepository) {
        this.ordemProducaoRepository = ordemProducaoRepository;
        this.produtoBaseRepository = produtoBaseRepository;
        this.fichaTecnicaRepository = fichaTecnicaRepository;
        this.estoqueMovimentacaoService = estoqueMovimentacaoService;
        this.produtoSkuRepository = produtoSkuRepository;
        this.pacoteRepository = pacoteRepository;
        this.cupomRepository = cupomRepository;
    }

    @Override
    @Transactional
    public OrdemProducaoResponse criarOrdemProducao(OrdemProducaoRequest request) {
        if (ordemProducaoRepository.existsByNumero(request.numero())) {
            throw new IllegalArgumentException("Ordem de Produção com número " + request.numero() + " já existe.");
        }

        ProdutoBase produto = produtoBaseRepository.findById(request.produtoBaseId())
                .orElseThrow(() -> new IllegalArgumentException("Produto base não encontrado."));

        FichaTecnica ficha = produto.getFichaTecnica();
        if (ficha == null) {
            throw new IllegalArgumentException("O produto selecionado não possui uma Ficha Técnica vinculada.");
        }

        OrdemProducao op = new OrdemProducao();
        op.setNumero(request.numero());
        op.setProdutoBase(produto);
        op.setFichaTecnica(ficha);
        op.setQuantidade(request.quantidade());
        op.setStatus(OrdemProducaoStatus.CADASTRADA);

        if (request.itens() != null && !request.itens().isEmpty()) {
            for (var itemReq : request.itens()) {
                ProdutoSku sku = produtoSkuRepository.findById(itemReq.produtoSkuId())
                        .orElseThrow(() -> new IllegalArgumentException("SKU não encontrado: " + itemReq.produtoSkuId()));
                OrdemProducaoItem item = new OrdemProducaoItem();
                item.setProdutoSku(sku);
                item.setQuantidade(itemReq.quantidade());
                op.addItem(item);
            }
        } else {
            if (produto.getSkus() != null && !produto.getSkus().isEmpty()) {
                // Distribui toda a quantidade para o primeiro SKU (comportamento padrão quando não detalhado)
                ProdutoSku primeiroSku = produto.getSkus().get(0);
                OrdemProducaoItem item = new OrdemProducaoItem();
                item.setProdutoSku(primeiroSku);
                item.setQuantidade(op.getQuantidade());
                op.addItem(item);
            } else {
                throw new IllegalArgumentException("O Produto Base não possui nenhuma Grade (SKU) cadastrada. Cadastre uma Grade no produto antes de criar a OP.");
            }
        }

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

    @Override
    @Transactional
    public void gerarPacotes(UUID id, int tamanhoPacote) {
        OrdemProducao op = ordemProducaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de Produção não encontrada."));

        if (op.getStatus() != OrdemProducaoStatus.CADASTRADA && op.getStatus() != OrdemProducaoStatus.EM_ANDAMENTO) {
            throw new IllegalStateException("Ordem de Produção não está em estado válido para gerar pacotes.");
        }

        // Verifica se já gerou pacotes
        List<Pacote> pacotesExistentes = pacoteRepository.findByOrdemProducaoId(id);
        if (!pacotesExistentes.isEmpty()) {
            throw new IllegalStateException("Pacotes já foram gerados para esta OP.");
        }
        
        if (op.getItens() == null || op.getItens().isEmpty()) {
            throw new IllegalStateException("Esta Ordem de Produção não possui Itens (SKUs) associados, portanto não é possível gerar pacotes físicos. Por favor, crie uma nova OP.");
        }

        int sequencialGlobal = 1;

        for (OrdemProducaoItem item : op.getItens()) {
            int qtdRestante = item.getQuantidade();

            while (qtdRestante > 0) {
                int qtdPacote = Math.min(qtdRestante, tamanhoPacote);
                
                Pacote pacote = new Pacote();
                pacote.setOrdemProducao(op);
                pacote.setProdutoSku(item.getProdutoSku());
                pacote.setSequencial(sequencialGlobal++);
                pacote.setQuantidadePecas(qtdPacote);
                
                Pacote savedPacote = pacoteRepository.save(pacote);

                // Gerar cupons para este pacote (um para cada operação da Ficha Técnica)
                int seqCupom = 1;
                for (FichaTecnicaOperacao operacao : op.getFichaTecnica().getOperacoes()) {
                    Cupom cupom = new Cupom();
                    cupom.setPacote(savedPacote);
                    cupom.setOperacao(operacao);
                    
                    // Código: OP_NUM-SEQ_PACOTE-SEQ_CUPOM
                    String codigoBarras = op.getNumero() + "-" + savedPacote.getSequencial() + "-" + (seqCupom++);
                    cupom.setCodigoBarras(codigoBarras);
                    
                    // Tempo total = tempo_unitario * quantidade_no_pacote
                    BigDecimal tempoUnitario = operacao.getTempoCalculadoCentesimal();
                    if(tempoUnitario == null) tempoUnitario = BigDecimal.ZERO;
                    cupom.setTempoTotalCentesimal(tempoUnitario.multiply(new BigDecimal(qtdPacote)));
                    cupom.setStatus(Cupom.Status.PENDENTE);
                    
                    cupomRepository.save(cupom);
                }

                qtdRestante -= qtdPacote;
            }
        }
    }

    private OrdemProducaoResponse mapToResponse(OrdemProducao op) {
        List<OrdemProducaoItemResponse> itens = null;
        if (op.getItens() != null) {
            itens = op.getItens().stream().map(item -> new OrdemProducaoItemResponse(
                    item.getId(),
                    item.getProdutoSku().getId(),
                    item.getProdutoSku().getCodigoBarras(),
                    item.getProdutoSku().getCor(),
                    item.getProdutoSku().getTamanho(),
                    item.getQuantidade()
            )).collect(Collectors.toList());
        }

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
                op.getDataInicio(),
                itens
        );
    }
}
