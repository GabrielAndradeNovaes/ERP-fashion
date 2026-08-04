package com.erp.catalog.service;

import com.erp.catalog.dto.ProdutoBaseRequest;
import com.erp.catalog.dto.ProdutoBaseResponse;

import java.util.List;
import java.util.UUID;

public interface ProdutoService {
    ProdutoBaseResponse createProduto(ProdutoBaseRequest request);
    List<ProdutoBaseResponse> getAllProdutos();
    ProdutoBaseResponse getProduto(UUID id);
    ProdutoBaseResponse updateProduto(UUID id, ProdutoBaseRequest request);
    void deleteProduto(UUID id);
}
