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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoBaseRepository produtoBaseRepository;

    public ProdutoServiceImpl(ProdutoBaseRepository produtoBaseRepository) {
        this.produtoBaseRepository = produtoBaseRepository;
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
        produtoBase.setCategoria(request.categoria());
        produtoBase.setColecao(request.colecao());
        produtoBase.setGenero(request.genero());
        produtoBase.setNcm(request.ncm());
        produtoBase.setCest(request.cest());
        produtoBase.setOrigem(request.origem());
        produtoBase.setPesoBruto(request.pesoBruto());
        produtoBase.setPesoLiquido(request.pesoLiquido());
        produtoBase.setStatus(request.status());

        if (request.skus() != null) {
            request.skus().forEach(skuDto -> {
                ProdutoSku sku = new ProdutoSku();
                sku.setCor(skuDto.cor());
                sku.setTamanho(skuDto.tamanho());
                sku.setCodigoBarras(skuDto.codigoBarras());
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
        produto.setCategoria(request.categoria());
        produto.setColecao(request.colecao());
        produto.setGenero(request.genero());
        produto.setNcm(request.ncm());
        produto.setCest(request.cest());
        produto.setOrigem(request.origem());
        produto.setPesoBruto(request.pesoBruto());
        produto.setPesoLiquido(request.pesoLiquido());
        produto.setStatus(request.status());

        // Processa as Grades (SKUs)
        if (request.skus() != null) {
            for (com.erp.catalog.dto.ProdutoSkuRequest skuDto : request.skus()) {
                // Verifica se a combinação de Cor + Tamanho já existe no produto
                java.util.Optional<ProdutoSku> skuExistenteOpt = produto.getSkus().stream()
                        .filter(s -> s.getCor().equalsIgnoreCase(skuDto.cor()) && s.getTamanho().equalsIgnoreCase(skuDto.tamanho()))
                        .findFirst();
                
                if (skuExistenteOpt.isPresent()) {
                    // Atualiza os dados do SKU existente (Preço e Código de Barras)
                    ProdutoSku skuExistente = skuExistenteOpt.get();
                    skuExistente.setCodigoBarras(skuDto.codigoBarras());
                    skuExistente.setPrecoVenda(skuDto.precoVenda());
                } else {
                    // Adiciona novo SKU
                    ProdutoSku novoSku = new ProdutoSku();
                    novoSku.setCor(skuDto.cor());
                    novoSku.setTamanho(skuDto.tamanho());
                    novoSku.setCodigoBarras(skuDto.codigoBarras());
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
                        sku.getCor(),
                        sku.getTamanho(),
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
                    produtoBase.getFichaTecnica().getMateriais().stream().map(m -> new com.erp.production.dto.FichaTecnicaMaterialResponse(m.getId(), m.getMaterial().getId(), m.getMaterial().getNome(), m.getMaterial().getUnidadeMedida(), m.getQuantidade())).collect(Collectors.toList()),
                    produtoBase.getFichaTecnica().getOperacoes().stream().map(op -> new com.erp.production.dto.FichaTecnicaOperacaoResponse(op.getId(), op.getNome(), op.getMaquina(), op.getOrdemExecucao(), op.getQuantidadeFolhas(), op.getQuantidadeParadas(), op.getGrauDificuldade(), op.getFaixaComprimento(), op.getTempoCalculadoCentesimal())).collect(Collectors.toList())
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
                produtoBase.getCategoria(),
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
