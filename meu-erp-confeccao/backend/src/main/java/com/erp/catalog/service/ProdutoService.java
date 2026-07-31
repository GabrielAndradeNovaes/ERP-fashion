package com.erp.catalog.service;

import com.erp.catalog.dto.ProdutoBaseRequest;
import com.erp.catalog.dto.ProdutoBaseResponse;

import java.util.List;

public interface ProdutoService {
    ProdutoBaseResponse createProduto(ProdutoBaseRequest request);
    List<ProdutoBaseResponse> getAllProdutos();
}
