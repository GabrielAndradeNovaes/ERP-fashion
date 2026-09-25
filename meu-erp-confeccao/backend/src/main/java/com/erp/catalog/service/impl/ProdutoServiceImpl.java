package com.erp.catalog.service.impl;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.domain.ProdutoSku;
import com.erp.catalog.dto.ProdutoBaseRequest;
import com.erp.catalog.dto.ProdutoBaseResponse;
import com.erp.catalog.dto.ProdutoSkuResponse;
import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.catalog.service.ProdutoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.erp.core.domain.Categoria;
import com.erp.core.repository.CategoriaRepository;
import com.erp.catalog.domain.Cor;
import com.erp.catalog.domain.Tamanho;
import com.erp.catalog.repository.CorRepository;
import com.erp.catalog.repository.TamanhoRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoBaseRepository produtoBaseRepository;
    private final CategoriaRepository categoriaRepository;
    private final CorRepository corRepository;
    private final TamanhoRepository tamanhoRepository;

    public ProdutoServiceImpl(
            ProdutoBaseRepository produtoBaseRepository,
            CategoriaRepository categoriaRepository,
            CorRepository corRepository,
            TamanhoRepository tamanhoRepository) {
        this.produtoBaseRepository = produtoBaseRepository;
        this.categoriaRepository = categoriaRepository;
        this.corRepository = corRepository;
        this.tamanhoRepository = tamanhoRepository;
    }

    @Override
    @Transactional
    public ProdutoBaseResponse createProduto(ProdutoBaseRequest request) {
        ProdutoBase produtoBase = new ProdutoBase();
        produtoBase.setCodigo(request.codigo());
        produtoBase.setNome(request.nome());
        produtoBase.setDescricao(request.descricao());
        produtoBase.setPrecoVenda(request.precoVenda());
        produtoBase.setPrecoCusto(request.precoCusto());
        produtoBase.setMarca(request.marca());
        produtoBase.setPesoLiquido(request.pesoLiquido());
        produtoBase.setStatus(request.status());
        
        if (request.categoriaId() != null) {
            produtoBase.setCategoria(categoriaRepository.findById(request.categoriaId()).orElse(null));
        }

        if (request.skus() != null) {
            request.skus().forEach(skuDto -> {
                ProdutoSku sku = new ProdutoSku();
                
                if (skuDto.corId() != null) {
                    sku.setCor(corRepository.findById(skuDto.corId()).orElse(null));
                }
                if (skuDto.tamanhoId() != null) {
                    sku.setTamanho(tamanhoRepository.findById(skuDto.tamanhoId()).orElse(null));
                }
                
                sku.setCodigoBarras((skuDto.codigoBarras() != null && skuDto.codigoBarras().isBlank()) ? null : skuDto.codigoBarras());
                sku.setPrecoVenda(skuDto.precoVenda());
                
                produtoBase.addSku(sku);
            });
        }

        ProdutoBase savedProduto = produtoBaseRepository.save(produtoBase);
        return mapToResponse(savedProduto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoBaseResponse> getAllProdutos() {
        return produtoBaseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoBaseResponse getProduto(UUID id) {
        ProdutoBase produto = produtoBaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        return mapToResponse(produto);
    }

    @Override
    @Transactional
    public ProdutoBaseResponse updateProduto(UUID id, ProdutoBaseRequest request) {
        ProdutoBase produto = produtoBaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        produto.setCodigo(request.codigo());
        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPrecoVenda(request.precoVenda());
        produto.setPrecoCusto(request.precoCusto());
        produto.setMarca(request.marca());
        produto.setPesoLiquido(request.pesoLiquido());
        produto.setStatus(request.status());

        if (request.categoriaId() != null) {
            produto.setCategoria(categoriaRepository.findById(request.categoriaId()).orElse(null));
        } else {
            produto.setCategoria(null);
        }

        // Processa as Grades (SKUs)
        if (request.skus() != null) {
            // Remove SKUs que não estão na nova lista
            java.util.List<ProdutoSku> skusParaRemover = produto.getSkus().stream()
                    .filter(s -> request.skus().stream().noneMatch(dto -> {
                        boolean corMatches = (s.getCor() != null && s.getCor().getId().equals(dto.corId())) || (s.getCor() == null && dto.corId() == null);
                        boolean tamanhoMatches = (s.getTamanho() != null && s.getTamanho().getId().equals(dto.tamanhoId())) || (s.getTamanho() == null && dto.tamanhoId() == null);
                        return corMatches && tamanhoMatches;
                    }))
                    .collect(java.util.stream.Collectors.toList());
            
            skusParaRemover.forEach(produto::removeSku);

            for (com.erp.catalog.dto.ProdutoSkuRequest skuDto : request.skus()) {
                // Verifica se a combinação de Cor + Tamanho já existe no produto
                java.util.Optional<ProdutoSku> skuExistenteOpt = produto.getSkus().stream()
                        .filter(s -> {
                            boolean corMatches = (s.getCor() != null && s.getCor().getId().equals(skuDto.corId())) || (s.getCor() == null && skuDto.corId() == null);
                            boolean tamanhoMatches = (s.getTamanho() != null && s.getTamanho().getId().equals(skuDto.tamanhoId())) || (s.getTamanho() == null && skuDto.tamanhoId() == null);
                            return corMatches && tamanhoMatches;
                        })
                        .findFirst();
                
                if (skuExistenteOpt.isPresent()) {
                    // Atualiza os dados do SKU existente (Preço e Código de Barras)
                    ProdutoSku skuExistente = skuExistenteOpt.get();
                    skuExistente.setCodigoBarras((skuDto.codigoBarras() != null && skuDto.codigoBarras().isBlank()) ? null : skuDto.codigoBarras());
                    skuExistente.setPrecoVenda(skuDto.precoVenda());
                } else {
                    // Adiciona novo SKU
                    ProdutoSku novoSku = new ProdutoSku();
                    if (skuDto.corId() != null) {
                        novoSku.setCor(corRepository.findById(skuDto.corId()).orElse(null));
                    }
                    if (skuDto.tamanhoId() != null) {
                        novoSku.setTamanho(tamanhoRepository.findById(skuDto.tamanhoId()).orElse(null));
                    }
                    novoSku.setCodigoBarras((skuDto.codigoBarras() != null && skuDto.codigoBarras().isBlank()) ? null : skuDto.codigoBarras());
                    novoSku.setPrecoVenda(skuDto.precoVenda());
                    produto.addSku(novoSku);
                }
            }
        }

        return mapToResponse(produtoBaseRepository.save(produto));
    }

    @Override
    @Transactional
    public void deleteProduto(UUID id) {
        ProdutoBase produto = produtoBaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        produtoBaseRepository.delete(produto);
    }

    private ProdutoBaseResponse mapToResponse(ProdutoBase produtoBase) {
        List<ProdutoSkuResponse> skuResponses = produtoBase.getSkus().stream()
                .map(sku -> new ProdutoSkuResponse(
                        sku.getId(),
                        sku.getCor() != null ? sku.getCor().getId() : null,
                        sku.getCor() != null ? sku.getCor().getNome() : null,
                        sku.getTamanho() != null ? sku.getTamanho().getId() : null,
                        sku.getTamanho() != null ? sku.getTamanho().getNome() : null,
                        sku.getCodigoBarras(),
                        sku.getPrecoVenda(),
                        sku.getQuantidadeAtual()
                ))
                .collect(Collectors.toList());

        com.erp.production.dto.FichaTecnicaResponse fichaTecnicaResponse = null;
        if (produtoBase.getFichaTecnica() != null) {
            fichaTecnicaResponse = new com.erp.production.dto.FichaTecnicaResponse(
                    produtoBase.getFichaTecnica().getId(),
                    produtoBase.getId(),
                    produtoBase.getNome(),
                    produtoBase.getFichaTecnica().getVersao(),
                    produtoBase.getFichaTecnica().getObservacoes(),
                    produtoBase.getFichaTecnica().getTempoPadraoTotalCentesimal(),
                    java.math.BigDecimal.ZERO, // getCustoTotalMateriais() not defined
                    produtoBase.getFichaTecnica().getMateriais().stream().map(m -> new com.erp.production.dto.FichaTecnicaMaterialResponse(m.getId(), m.getMaterial().getId(), m.getMaterial().getNome(), m.getMaterial().getUnidadeMedida() != null ? m.getMaterial().getUnidadeMedida().getNome() : null, m.getQuantidade())).collect(Collectors.toList()),
                    produtoBase.getFichaTecnica().getOperacoes().stream().map(op -> new com.erp.production.dto.FichaTecnicaOperacaoResponse(op.getId(), op.getNome(), op.getMaquina(), op.getOrdemExecucao(), op.getQuantidadeFolhas(), op.getQuantidadeParadas(), op.getRpmMaquina(), op.getPontosPorCm(), op.getComprimentoCosturaCm(), op.getTipoTrajeto(), op.getDificuldadeTecido(), op.getSamMinutos(), op.getTempoCalculadoCentesimal())).collect(Collectors.toList())
            );
        }

        return new ProdutoBaseResponse(
                produtoBase.getId(),
                produtoBase.getCodigo(),
                produtoBase.getNome(),
                produtoBase.getDescricao(),
                produtoBase.getPrecoVenda(),
                produtoBase.getPrecoCusto(),
                produtoBase.getMarca(),
                produtoBase.getCategoria() != null ? produtoBase.getCategoria().getId() : null,
                produtoBase.getCategoria() != null ? produtoBase.getCategoria().getNome() : null,
                produtoBase.getColecao(),
                produtoBase.getGenero(),
                produtoBase.getNcm(),
                produtoBase.getCest(),
                produtoBase.getOrigem(),
                produtoBase.getPesoBruto(),
                produtoBase.getPesoLiquido(),
                produtoBase.getStatus(),
                skuResponses,
                fichaTecnicaResponse
        );
    }
}
