package com.erp.finance.controller;

import com.erp.finance.domain.TituloPagar;
import com.erp.finance.service.FinanceiroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/financeiro/titulos")
public class TituloPagarController {

    private final FinanceiroService financeiroService;

    public TituloPagarController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    @GetMapping
    public ResponseEntity<List<TituloPagar>> listarTitulos() {
        return ResponseEntity.ok(financeiroService.listarTitulos());
    }

    @PostMapping("/{id}/baixar")
    public ResponseEntity<Void> baixarTitulo(@PathVariable UUID id) {
        financeiroService.baixarTitulo(id);
        return ResponseEntity.ok().build();
    }
}
