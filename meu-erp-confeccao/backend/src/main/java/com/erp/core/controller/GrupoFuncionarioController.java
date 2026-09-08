package com.erp.core.controller;

import com.erp.core.domain.GrupoFuncionario;
import com.erp.core.domain.Empresa;
import com.erp.core.repository.GrupoFuncionarioRepository;
import com.erp.core.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grupos-funcionarios")
public class GrupoFuncionarioController {

    private final GrupoFuncionarioRepository repository;
    private final EntityManager entityManager;

    public GrupoFuncionarioController(GrupoFuncionarioRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @GetMapping
    public List<GrupoFuncionario> listar() {
        return repository.findAll();
    }

    @PostMapping
    public GrupoFuncionario criar(@RequestBody GrupoFuncionario grupo) {
        Empresa empresa = entityManager.getReference(Empresa.class, TenantContext.getCurrentTenant());
        grupo.setEmpresa(empresa);
        return repository.save(grupo);
    }
}
