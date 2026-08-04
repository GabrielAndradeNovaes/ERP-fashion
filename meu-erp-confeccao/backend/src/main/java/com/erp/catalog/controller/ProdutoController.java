package com.erp.catalog.controller;

import com.erp.catalog.dto.ProdutoBaseRequest;
import com.erp.catalog.dto.ProdutoBaseResponse;
import com.erp.catalog.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoBaseResponse> getProduto(@PathVariable UUID id) {
        ProdutoBaseResponse response = produtoService.getProduto(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoBaseResponse> updateProduto(@PathVariable UUID id, @RequestBody ProdutoBaseRequest request) {
        ProdutoBaseResponse response = produtoService.updateProduto(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable UUID id) {
        produtoService.deleteProduto(id);
        return ResponseEntity.noContent().build();
    }
}
