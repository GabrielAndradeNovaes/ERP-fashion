package com.erp.catalog.controller;

import com.erp.catalog.domain.UnidadeMedida;
import com.erp.catalog.repository.UnidadeMedidaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/unidades-medida")
public class UnidadeMedidaController {

    private final UnidadeMedidaRepository unidadeMedidaRepository;

    public UnidadeMedidaController(UnidadeMedidaRepository unidadeMedidaRepository) {
        this.unidadeMedidaRepository = unidadeMedidaRepository;
    }

    @PostMapping
    public ResponseEntity<UnidadeMedida> create(@RequestBody UnidadeMedida unidadeMedida) {
        return new ResponseEntity<>(unidadeMedidaRepository.save(unidadeMedida), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UnidadeMedida>> getAll() {
        return ResponseEntity.ok(unidadeMedidaRepository.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadeMedida> update(@PathVariable UUID id, @RequestBody UnidadeMedida req) {
        return unidadeMedidaRepository.findById(id).map(unidadeMedida -> {
            unidadeMedida.setNome(req.getNome());
            unidadeMedida.setSigla(req.getSigla());
            unidadeMedida.setAtivo(req.getAtivo());
            return ResponseEntity.ok(unidadeMedidaRepository.save(unidadeMedida));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (unidadeMedidaRepository.existsById(id)) {
            unidadeMedidaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
