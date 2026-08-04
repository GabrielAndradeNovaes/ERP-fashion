package com.erp.core.service.impl;

import com.erp.core.domain.Categoria;
import com.erp.core.dto.CategoriaRequest;
import com.erp.core.dto.CategoriaResponse;
import com.erp.core.repository.CategoriaRepository;
import com.erp.core.service.CategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaServiceImpl(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public CategoriaResponse create(CategoriaRequest request) {
        Categoria entity = new Categoria();
        entity.setNome(request.nome());
        entity.setTipo(request.tipo());
        return mapToResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> getAll() {
        return repository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse getById(UUID id) {
        return repository.findById(id).map(this::mapToResponse).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }

    @Override
    @Transactional
    public CategoriaResponse update(UUID id, CategoriaRequest request) {
        Categoria entity = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        entity.setNome(request.nome());
        entity.setTipo(request.tipo());
        return mapToResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private CategoriaResponse mapToResponse(Categoria entity) {
        return new CategoriaResponse(
                entity.getId(),
                entity.getNome(),
                null,
                null,
                null,
                entity.getTipo(),
                null
        );
    }
}
