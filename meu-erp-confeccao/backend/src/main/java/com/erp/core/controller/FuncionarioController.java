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
    private final com.erp.core.repository.GrupoFuncionarioRepository grupoFuncionarioRepository;
    private final jakarta.persistence.EntityManager entityManager;

    public FuncionarioController(FuncionarioRepository funcionarioRepository, com.erp.core.repository.GrupoFuncionarioRepository grupoFuncionarioRepository, jakarta.persistence.EntityManager entityManager) {
        this.funcionarioRepository = funcionarioRepository;
        this.grupoFuncionarioRepository = grupoFuncionarioRepository;
        this.entityManager = entityManager;
    }

    private com.erp.core.domain.GrupoFuncionario resolveGrupo(com.erp.core.domain.GrupoFuncionario grupo) {
        if (grupo == null) return null;
        if (grupo.getId() != null) return grupo;
        if (grupo.getNome() != null && !grupo.getNome().isEmpty()) {
            java.util.List<com.erp.core.domain.GrupoFuncionario> existentes = grupoFuncionarioRepository.findByNomeContainingIgnoreCase(grupo.getNome());
            for (com.erp.core.domain.GrupoFuncionario g : existentes) {
                if (g.getNome().equalsIgnoreCase(grupo.getNome())) return g;
            }
            com.erp.core.domain.GrupoFuncionario novo = new com.erp.core.domain.GrupoFuncionario();
            novo.setNome(grupo.getNome());
            novo.setEmpresa(entityManager.getReference(com.erp.core.domain.Empresa.class, com.erp.core.tenant.TenantContext.getCurrentTenant()));
            return grupoFuncionarioRepository.save(novo);
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<Funcionario>> listarTodos() {
        return ResponseEntity.ok(funcionarioRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Funcionario> criar(@RequestBody Funcionario funcionario) {
        funcionario.setGrupo(resolveGrupo(funcionario.getGrupo()));
        if (funcionario.getJornadas() != null) {
            funcionario.getJornadas().forEach(j -> j.setFuncionario(funcionario));
        }
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
            existente.setMetaMinima(funcionario.getMetaMinima());
            existente.setPremio100(funcionario.getPremio100());
            existente.setGrupo(resolveGrupo(funcionario.getGrupo()));
            
            existente.getJornadas().clear();
            if (funcionario.getJornadas() != null) {
                for (com.erp.core.domain.FuncionarioJornada j : funcionario.getJornadas()) {
                    j.setFuncionario(existente);
                    existente.getJornadas().add(j);
                }
            }

            return ResponseEntity.ok(funcionarioRepository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/copiar-jornada")
    public ResponseEntity<Void> copiarJornada(@PathVariable java.util.UUID id) {
        Funcionario origem = funcionarioRepository.findById(id).orElseThrow();
        if (origem.getGrupo() == null) return ResponseEntity.badRequest().build();
        
        List<Funcionario> todosNoGrupo = funcionarioRepository.findByGrupoId(origem.getGrupo().getId());
        for (Funcionario f : todosNoGrupo) {
            if (f.getId().equals(origem.getId())) continue;
            
            f.getJornadas().clear();
            for (com.erp.core.domain.FuncionarioJornada j : origem.getJornadas()) {
                com.erp.core.domain.FuncionarioJornada copia = new com.erp.core.domain.FuncionarioJornada();
                copia.setFuncionario(f);
                copia.setDiaSemana(j.getDiaSemana());
                copia.setEntrada(j.getEntrada());
                copia.setSaida(j.getSaida());
                f.getJornadas().add(copia);
            }
        }
        funcionarioRepository.saveAll(todosNoGrupo);
        return ResponseEntity.ok().build();
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
