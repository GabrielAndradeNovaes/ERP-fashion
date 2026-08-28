package com.erp.catalog.controller;

import com.erp.catalog.domain.Cor;
import com.erp.catalog.repository.CorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/cores")
public class CorController {

    private final CorRepository corRepository;

    public CorController(CorRepository corRepository) {
        this.corRepository = corRepository;
    }

    @PostMapping
    public ResponseEntity<Cor> create(@RequestBody Cor cor) {
        return new ResponseEntity<>(corRepository.save(cor), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Cor>> getAll() {
        return ResponseEntity.ok(corRepository.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cor> update(@PathVariable UUID id, @RequestBody Cor corReq) {
        return corRepository.findById(id).map(cor -> {
            cor.setNome(corReq.getNome());
            cor.setCodigoHex(corReq.getCodigoHex());
            cor.setAtivo(corReq.getAtivo());
            return ResponseEntity.ok(corRepository.save(cor));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (corRepository.existsById(id)) {
            corRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
