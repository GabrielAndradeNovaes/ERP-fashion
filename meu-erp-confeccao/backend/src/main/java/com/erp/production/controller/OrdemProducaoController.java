package com.erp.production.controller;

import com.erp.production.dto.OrdemProducaoRequest;
import com.erp.production.dto.OrdemProducaoResponse;
import com.erp.production.service.OrdemProducaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.erp.core.service.SequenceGeneratorService;

@RestController
@RequestMapping("/api/production/ordens")
@CrossOrigin(origins = "*") // Para desenvolvimento local
public class OrdemProducaoController {

    private final OrdemProducaoService ordemProducaoService;
    private final SequenceGeneratorService sequenceGeneratorService;

    public OrdemProducaoController(OrdemProducaoService ordemProducaoService, SequenceGeneratorService sequenceGeneratorService) {
        this.ordemProducaoService = ordemProducaoService;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @GetMapping("/next-numero")
    public ResponseEntity<String> getNextNumero() {
        return ResponseEntity.ok(sequenceGeneratorService.getNextSequence("ordens_producao", "numero"));
    }

    @PostMapping
    public ResponseEntity<OrdemProducaoResponse> criar(@RequestBody OrdemProducaoRequest request) {
        return ResponseEntity.ok(ordemProducaoService.criarOrdemProducao(request));
    }

    @GetMapping
    public ResponseEntity<List<OrdemProducaoResponse>> listarTodas() {
        return ResponseEntity.ok(ordemProducaoService.listarTodas());
    }

    @GetMapping("/search")
    public ResponseEntity<org.springframework.data.domain.Page<OrdemProducaoResponse>> buscarComFiltros(
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String produtoId,
            @RequestParam(required = false) com.erp.production.domain.OrdemProducaoStatus status,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate dataInicio,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate dataFim,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(ordemProducaoService.buscarComFiltros(numero, produtoId, status, dataInicio, dataFim, pageable));
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

    @PutMapping("/{id}")
    public ResponseEntity<OrdemProducaoResponse> atualizar(
            @PathVariable UUID id,
            @RequestBody OrdemProducaoRequest request) {
        return ResponseEntity.ok(ordemProducaoService.atualizarOrdemProducao(id, request));
    }

    @PostMapping("/{id}/estornar")
    public ResponseEntity<OrdemProducaoResponse> estornar(@PathVariable UUID id) {
        return ResponseEntity.ok(ordemProducaoService.estornarOrdemProducao(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        ordemProducaoService.excluirOrdemProducao(id);
        return ResponseEntity.noContent().build();
    }
}
