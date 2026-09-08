package com.erp.production.controller;

import com.erp.production.dto.ProdutividadeResumo;
import com.erp.production.service.ProdutividadeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/production/produtividade")
public class ProdutividadeController {

    private final ProdutividadeService produtividadeService;

    public ProdutividadeController(ProdutividadeService produtividadeService) {
        this.produtividadeService = produtividadeService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutividadeResumo>> getResumo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(produtividadeService.getResumo(start, end));
    }

    public record PagamentoRequest(UUID funcionarioId, BigDecimal valorPagar) {}

    @PostMapping("/pagar")
    public ResponseEntity<Void> pagar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestBody List<PagamentoRequest> pagamentos) {
        
        for (PagamentoRequest pag : pagamentos) {
            produtividadeService.pagarProdutividade(pag.funcionarioId(), start, end, pag.valorPagar());
        }
        return ResponseEntity.ok().build();
    }
}
