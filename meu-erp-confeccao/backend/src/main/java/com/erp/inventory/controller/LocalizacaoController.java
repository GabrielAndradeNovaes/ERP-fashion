package com.erp.inventory.controller;

import com.erp.inventory.domain.Localizacao;
import com.erp.inventory.repository.LocalizacaoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory/localizacoes")
public class LocalizacaoController {

    private final LocalizacaoRepository localizacaoRepository;

    public LocalizacaoController(LocalizacaoRepository localizacaoRepository) {
        this.localizacaoRepository = localizacaoRepository;
    }

    @PostMapping
    public ResponseEntity<Localizacao> create(@RequestBody Localizacao localizacao) {
        return new ResponseEntity<>(localizacaoRepository.save(localizacao), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Localizacao>> getAll() {
        return ResponseEntity.ok(localizacaoRepository.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Localizacao> update(@PathVariable UUID id, @RequestBody Localizacao req) {
        return localizacaoRepository.findById(id).map(localizacao -> {
            localizacao.setNome(req.getNome());
            localizacao.setTipo(req.getTipo());
            localizacao.setAtivo(req.getAtivo());
            return ResponseEntity.ok(localizacaoRepository.save(localizacao));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (localizacaoRepository.existsById(id)) {
            localizacaoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
