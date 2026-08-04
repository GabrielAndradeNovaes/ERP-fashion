package com.erp.core.service.impl;

import com.erp.core.domain.UnidadeMedida;
import com.erp.core.dto.UnidadeMedidaRequest;
import com.erp.core.dto.UnidadeMedidaResponse;
import com.erp.core.repository.UnidadeMedidaRepository;
import com.erp.core.service.UnidadeMedidaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UnidadeMedidaServiceImpl implements UnidadeMedidaService {

    private final UnidadeMedidaRepository repository;

    public UnidadeMedidaServiceImpl(UnidadeMedidaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public UnidadeMedidaResponse create(UnidadeMedidaRequest request) {
        UnidadeMedida entity = new UnidadeMedida();
        entity.setNome(request.nome());
        entity.setSigla(request.sigla());
        return mapToResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadeMedidaResponse> getAll() {
        return repository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadeMedidaResponse getById(UUID id) {
        return repository.findById(id).map(this::mapToResponse).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }

    @Override
    @Transactional
    public UnidadeMedidaResponse update(UUID id, UnidadeMedidaRequest request) {
        UnidadeMedida entity = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
        entity.setNome(request.nome());
        entity.setSigla(request.sigla());
        return mapToResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private UnidadeMedidaResponse mapToResponse(UnidadeMedida entity) {
        return new UnidadeMedidaResponse(
                entity.getId(),
                entity.getNome(),
                null,
                null,
                null,
                null,
                entity.getSigla()
        );
    }
}
