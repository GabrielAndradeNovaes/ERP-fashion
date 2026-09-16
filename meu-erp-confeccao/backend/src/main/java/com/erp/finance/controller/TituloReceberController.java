package com.erp.finance.controller;

import com.erp.finance.domain.TituloReceber;
import com.erp.finance.service.FinanceiroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/financeiro/receber")
public class TituloReceberController {

    private final FinanceiroService financeiroService;

    public TituloReceberController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }

    @GetMapping
    public ResponseEntity<List<TituloReceber>> listarTitulosReceber() {
        return ResponseEntity.ok(financeiroService.listarTitulosReceber());
    }

    @PostMapping("/{id}/baixar")
    public ResponseEntity<Void> baixarTituloReceber(@PathVariable UUID id) {
        financeiroService.baixarTituloReceber(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<com.erp.finance.domain.TituloReceber> criarTituloManual(@RequestBody com.erp.finance.dto.TituloRequest request) {
        return ResponseEntity.ok(financeiroService.criarTituloReceberManual(request));
    }
}
