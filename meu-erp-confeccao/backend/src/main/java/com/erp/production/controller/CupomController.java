package com.erp.production.controller;

import com.erp.production.dto.CupomResponse;
import com.erp.production.service.CupomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/production/cupons")
@CrossOrigin(origins = "*") // Para desenvolvimento local
public class CupomController {

    private final CupomService cupomService;

    public CupomController(CupomService cupomService) {
        this.cupomService = cupomService;
    }

    @GetMapping("/ordem/{opId}")
    public ResponseEntity<List<CupomResponse>> listarPorOrdemProducao(@PathVariable UUID opId) {
        return ResponseEntity.ok(cupomService.listarPorOrdemProducao(opId));
    }
}
