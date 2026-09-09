package com.erp.production.controller;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Funcionario;
import com.erp.core.repository.FuncionarioRepository;
import com.erp.core.tenant.EmpresaContext;
import com.erp.production.domain.ApontamentoManual;
import com.erp.production.dto.ApontamentoManualRequest;
import com.erp.production.repository.ApontamentoManualRepository;
import jakarta.persistence.EntityManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/production/apontamentos-manuais")
public class ApontamentoManualController {

    private final ApontamentoManualRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final EntityManager entityManager;

    public ApontamentoManualController(ApontamentoManualRepository repository, FuncionarioRepository funcionarioRepository, EntityManager entityManager) {
        this.repository = repository;
        this.funcionarioRepository = funcionarioRepository;
        this.entityManager = entityManager;
    }

    @PostMapping
    public ResponseEntity<ApontamentoManual> criar(@RequestBody ApontamentoManualRequest request) {
        Funcionario funcionario = funcionarioRepository.findById(request.funcionarioId())
            .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));

        ApontamentoManual apontamento = new ApontamentoManual();
        apontamento.setFuncionario(funcionario);
        apontamento.setMinutos(request.minutos());
        apontamento.setObservacao(request.observacao());
        apontamento.setDataHora(LocalDateTime.now());

        List<UUID> empresas = EmpresaContext.getEmpresas();
        if (empresas != null && !empresas.isEmpty()) {
            Empresa empresa = entityManager.getReference(Empresa.class, empresas.get(0));
            apontamento.setEmpresa(empresa);
        }

        ApontamentoManual salvo = repository.save(apontamento);
        return ResponseEntity.ok(salvo);
    }
}
