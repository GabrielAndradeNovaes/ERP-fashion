package com.erp.core.controller;

import com.erp.core.domain.Funcionario;
import com.erp.core.repository.FuncionarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioController(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Funcionario>> listarTodos() {
        return ResponseEntity.ok(funcionarioRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Funcionario> criar(@RequestBody Funcionario funcionario) {
        return ResponseEntity.ok(funcionarioRepository.save(funcionario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizar(@PathVariable java.util.UUID id, @RequestBody Funcionario funcionario) {
        return funcionarioRepository.findById(id).map(existente -> {
            existente.setNome(funcionario.getNome());
            existente.setMatricula(funcionario.getMatricula());
            existente.setCargaHorariaDiariaPadrao(funcionario.getCargaHorariaDiariaPadrao());
            existente.setCargaHorariaMensalPadrao(funcionario.getCargaHorariaMensalPadrao());
            existente.setAtivo(funcionario.getAtivo());
            return ResponseEntity.ok(funcionarioRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable java.util.UUID id) {
        if (funcionarioRepository.existsById(id)) {
            funcionarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
