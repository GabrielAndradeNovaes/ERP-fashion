package com.erp.core.service.impl;

import com.erp.core.domain.Fornecedor;
import com.erp.core.dto.FornecedorRequest;
import com.erp.core.dto.FornecedorResponse;
import com.erp.core.repository.FornecedorRepository;
import com.erp.core.service.FornecedorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FornecedorServiceImpl implements FornecedorService {

    private final FornecedorRepository repository;
    private final jakarta.persistence.EntityManager entityManager;

    public FornecedorServiceImpl(FornecedorRepository repository, jakarta.persistence.EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public FornecedorResponse create(FornecedorRequest request) {
        Fornecedor entity = new Fornecedor();
        entity.setNome(request.nome());
        entity.setDocumento(request.documento());
        entity.setEmail(request.email());
        entity.setTelefone(request.telefone());
        entity.setTipoPessoa(request.tipoPessoa());
        entity.setRazaoSocial(request.razaoSocial());
        entity.setInscricaoEstadual(request.inscricaoEstadual());
        entity.setCategoriaFornecedor(request.categoriaFornecedor());
        entity.setPrazoPagamentoPadrao(request.prazoPagamentoPadrao());
        entity.setContatoNome(request.contatoNome());
        entity.setStatus(request.status());
        String endereco = request.endereco();
        if (endereco != null && endereco.trim().isEmpty()) {
            endereco = null;
        }
        entity.setEndereco(endereco);

        java.util.List<UUID> empresas = com.erp.core.tenant.EmpresaContext.getEmpresas();
        if (empresas != null && !empresas.isEmpty()) {
            entity.setEmpresa(entityManager.getReference(com.erp.core.domain.Empresa.class, empresas.get(0)));
        } else {
            java.util.List<com.erp.core.domain.Empresa> todasEmpresas = entityManager.createQuery("SELECT e FROM Empresa e", com.erp.core.domain.Empresa.class).getResultList();
            if (!todasEmpresas.isEmpty()) {
                entity.setEmpresa(todasEmpresas.get(0));
            }
        }

        return mapToResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FornecedorResponse> getAll() {
        return repository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FornecedorResponse getById(UUID id) {
        return repository.findById(id).map(this::mapToResponse).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }

    @Override
    @Transactional
    public FornecedorResponse update(UUID id, FornecedorRequest request) {
        Fornecedor entity = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        entity.setNome(request.nome());
        entity.setDocumento(request.documento());
        entity.setEmail(request.email());
        entity.setTelefone(request.telefone());
        entity.setTipoPessoa(request.tipoPessoa());
        entity.setRazaoSocial(request.razaoSocial());
        entity.setInscricaoEstadual(request.inscricaoEstadual());
        entity.setCategoriaFornecedor(request.categoriaFornecedor());
        entity.setPrazoPagamentoPadrao(request.prazoPagamentoPadrao());
        entity.setContatoNome(request.contatoNome());
        entity.setStatus(request.status());
        String endereco = request.endereco();
        if (endereco != null && endereco.trim().isEmpty()) {
            endereco = null;
        }
        entity.setEndereco(endereco);
        
        return mapToResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private FornecedorResponse mapToResponse(Fornecedor entity) {
        return new FornecedorResponse(
                entity.getId(),
                entity.getNome(),
                entity.getDocumento(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getTipoPessoa(),
                entity.getRazaoSocial(),
                entity.getInscricaoEstadual(),
                entity.getCategoriaFornecedor(),
                entity.getPrazoPagamentoPadrao(),
                entity.getContatoNome(),
                entity.getStatus(),
                entity.getEndereco()
        );
    }
}
