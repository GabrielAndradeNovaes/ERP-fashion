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

    public FornecedorServiceImpl(FornecedorRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public FornecedorResponse create(FornecedorRequest request) {
        Fornecedor entity = new Fornecedor();
        entity.setNome(request.nome());
        entity.setDocumento(request.documento());
        entity.setEmail(request.email());
        entity.setTelefone(request.telefone());
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
                null,
                null
        );
    }
}
