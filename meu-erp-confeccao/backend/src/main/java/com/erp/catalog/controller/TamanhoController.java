package com.erp.catalog.controller;

import com.erp.catalog.domain.Tamanho;
import com.erp.catalog.repository.TamanhoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/tamanhos")
public class TamanhoController {

    private final TamanhoRepository tamanhoRepository;

    public TamanhoController(TamanhoRepository tamanhoRepository) {
        this.tamanhoRepository = tamanhoRepository;
    }

    @PostMapping
    public ResponseEntity<Tamanho> create(@RequestBody Tamanho tamanho) {
        return new ResponseEntity<>(tamanhoRepository.save(tamanho), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Tamanho>> getAll() {
        return ResponseEntity.ok(tamanhoRepository.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tamanho> update(@PathVariable UUID id, @RequestBody Tamanho req) {
        return tamanhoRepository.findById(id).map(tamanho -> {
            tamanho.setNome(req.getNome());
            tamanho.setSigla(req.getSigla());
            tamanho.setAtivo(req.getAtivo());
            return ResponseEntity.ok(tamanhoRepository.save(tamanho));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (tamanhoRepository.existsById(id)) {
            tamanhoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
