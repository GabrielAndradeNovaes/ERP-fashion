package com.erp.production.controller;

import com.erp.production.dto.OrdemProducaoRequest;
import com.erp.production.dto.OrdemProducaoResponse;
import com.erp.production.service.OrdemProducaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/production/ordens")
@CrossOrigin(origins = "*") // Para desenvolvimento local
public class OrdemProducaoController {

    private final OrdemProducaoService ordemProducaoService;

    public OrdemProducaoController(OrdemProducaoService ordemProducaoService) {
        this.ordemProducaoService = ordemProducaoService;
    }

    @PostMapping
    public ResponseEntity<OrdemProducaoResponse> criar(@RequestBody OrdemProducaoRequest request) {
        return ResponseEntity.ok(ordemProducaoService.criarOrdemProducao(request));
    }

    @GetMapping
    public ResponseEntity<List<OrdemProducaoResponse>> listarTodas() {
        return ResponseEntity.ok(ordemProducaoService.listarTodas());
    }

    @PutMapping("/{id}/iniciar")
    public ResponseEntity<OrdemProducaoResponse> iniciarProducao(@PathVariable UUID id) {
        return ResponseEntity.ok(ordemProducaoService.iniciarProducao(id));
    }
}
