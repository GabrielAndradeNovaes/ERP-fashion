package com.erp.production.controller;

import com.erp.production.dto.TabelaTempoPadraoRequest;
import com.erp.production.dto.TabelaTempoPadraoResponse;
import com.erp.production.service.TabelaTempoPadraoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/production/tempos-padrao")
public class TabelaTempoPadraoController {

    private final TabelaTempoPadraoService service;

    public TabelaTempoPadraoController(TabelaTempoPadraoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TabelaTempoPadraoResponse> create(@RequestBody TabelaTempoPadraoRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TabelaTempoPadraoResponse> update(@PathVariable UUID id, @RequestBody TabelaTempoPadraoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping
    public ResponseEntity<List<TabelaTempoPadraoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
