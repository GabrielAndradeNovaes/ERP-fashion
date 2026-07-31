package com.erp.catalog.controller;

import com.erp.catalog.dto.ProdutoBaseRequest;
import com.erp.catalog.dto.ProdutoBaseResponse;
import com.erp.catalog.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<ProdutoBaseResponse> createProduto(@RequestBody ProdutoBaseRequest request) {
        ProdutoBaseResponse response = produtoService.createProduto(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProdutoBaseResponse>> getAllProdutos() {
        List<ProdutoBaseResponse> responses = produtoService.getAllProdutos();
        return ResponseEntity.ok(responses);
    }
}
