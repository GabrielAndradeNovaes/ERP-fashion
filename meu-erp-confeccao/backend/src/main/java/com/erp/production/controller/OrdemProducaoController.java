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

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<OrdemProducaoResponse> iniciarProducao(@PathVariable UUID id) {
        return ResponseEntity.ok(ordemProducaoService.iniciarProducao(id));
    }

    @PostMapping("/{id}/gerar-pacotes")
    public ResponseEntity<Void> gerarPacotes(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "20") int tamanhoPacote) {
        ordemProducaoService.gerarPacotes(id, tamanhoPacote);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrdemProducaoResponse> atualizarStatus(
            @PathVariable UUID id, 
            @RequestBody java.util.Map<String, String> payload) {
        String novoStatus = payload.get("status");
        com.erp.production.domain.OrdemProducaoStatus statusEnum = 
            com.erp.production.domain.OrdemProducaoStatus.valueOf(novoStatus.toUpperCase());
        return ResponseEntity.ok(ordemProducaoService.atualizarStatus(id, statusEnum));
    }
}
