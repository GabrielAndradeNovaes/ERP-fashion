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

    private ProdutoBaseResponse mapToResponse(ProdutoBase produtoBase) {
        List<ProdutoSkuResponse> skuResponses = produtoBase.getSkus().stream()
                .map(sku -> new ProdutoSkuResponse(
                        sku.getId(),
                        sku.getCor(),
                        sku.getTamanho(),
                        sku.getCodigoBarras(),
                        sku.getPrecoVenda()
                ))
                .collect(Collectors.toList());

        return new ProdutoBaseResponse(
                produtoBase.getId(),
                produtoBase.getCodigo(),
                produtoBase.getNome(),
                produtoBase.getDescricao(),
                skuResponses
        );
    }
}
