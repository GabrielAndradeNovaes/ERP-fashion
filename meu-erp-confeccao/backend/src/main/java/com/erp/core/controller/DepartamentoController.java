package com.erp.core.controller;

import com.erp.core.domain.Departamento;
import com.erp.core.repository.DepartamentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/core/departamentos")
public class DepartamentoController {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoController(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    @PostMapping
    public ResponseEntity<Departamento> create(@RequestBody Departamento departamento) {
        return new ResponseEntity<>(departamentoRepository.save(departamento), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Departamento>> getAll() {
        return ResponseEntity.ok(departamentoRepository.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Departamento> update(@PathVariable UUID id, @RequestBody Departamento req) {
        return departamentoRepository.findById(id).map(departamento -> {
            departamento.setNome(req.getNome());
            departamento.setAtivo(req.getAtivo());
            return ResponseEntity.ok(departamentoRepository.save(departamento));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (departamentoRepository.existsById(id)) {
            departamentoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
