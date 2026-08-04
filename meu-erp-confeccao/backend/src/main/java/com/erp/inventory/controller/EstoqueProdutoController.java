package com.erp.inventory.controller;

import com.erp.inventory.domain.EstoqueProdutoMovimentacao;
import com.erp.inventory.dto.ProdutoMovimentacaoRequest;
import com.erp.inventory.service.EstoqueProdutoMovimentacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory/produtos-skus")
public class EstoqueProdutoController {

    private final EstoqueProdutoMovimentacaoService movimentacaoService;

    public EstoqueProdutoController(EstoqueProdutoMovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @PostMapping("/{skuId}/movimentacoes")
    public ResponseEntity<EstoqueProdutoMovimentacao> registrarMovimentacao(
            @PathVariable UUID skuId,
            @RequestBody ProdutoMovimentacaoRequest request) {
        EstoqueProdutoMovimentacao mov = movimentacaoService.registrarMovimentacao(
                skuId,
                request.getTipo(),
                request.getQuantidade(),
                request.getDocumentoReferencia()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mov);
    }

    @GetMapping("/{skuId}/movimentacoes")
    public ResponseEntity<List<EstoqueProdutoMovimentacao>> listarHistorico(@PathVariable UUID skuId) {
        return ResponseEntity.ok(movimentacaoService.listarHistoricoPorSku(skuId));
    }
}
