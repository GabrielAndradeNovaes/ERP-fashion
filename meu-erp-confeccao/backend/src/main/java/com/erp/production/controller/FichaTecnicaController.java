package com.erp.production.controller;

import com.erp.production.dto.FichaTecnicaRequest;
import com.erp.production.dto.FichaTecnicaResponse;
import com.erp.production.service.FichaTecnicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/production/fichas-tecnicas")
public class FichaTecnicaController {

    private final FichaTecnicaService fichaTecnicaService;

    public FichaTecnicaController(FichaTecnicaService fichaTecnicaService) {
        this.fichaTecnicaService = fichaTecnicaService;
    }

    @PostMapping
    public ResponseEntity<FichaTecnicaResponse> createFichaTecnica(@RequestBody FichaTecnicaRequest request) {
        FichaTecnicaResponse response = fichaTecnicaService.createFichaTecnica(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/produto/{produtoBaseId}")
    public ResponseEntity<List<FichaTecnicaResponse>> getFichasPorProduto(@PathVariable UUID produtoBaseId) {
        List<FichaTecnicaResponse> responses = fichaTecnicaService.getFichasPorProduto(produtoBaseId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichaTecnicaResponse> getFichaTecnicaById(@PathVariable UUID id) {
        FichaTecnicaResponse response = fichaTecnicaService.getFichaTecnicaById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{fichaTecnicaId}/operacoes")
    public ResponseEntity<FichaTecnicaResponse> addOperacao(
            @PathVariable UUID fichaTecnicaId, 
            @RequestBody com.erp.production.dto.FichaTecnicaOperacaoRequest request) {
        FichaTecnicaResponse response = fichaTecnicaService.addOperacao(fichaTecnicaId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{fichaTecnicaId}/operacoes/{operacaoId}")
    public ResponseEntity<FichaTecnicaResponse> removeOperacao(
            @PathVariable UUID fichaTecnicaId, 
            @PathVariable UUID operacaoId) {
        FichaTecnicaResponse response = fichaTecnicaService.removeOperacao(fichaTecnicaId, operacaoId);
        return ResponseEntity.ok(response);
    }
}
