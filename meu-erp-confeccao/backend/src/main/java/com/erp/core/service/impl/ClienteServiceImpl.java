package com.erp.core.service.impl;

import com.erp.core.domain.Cliente;
import com.erp.core.dto.ClienteRequest;
import com.erp.core.dto.ClienteResponse;
import com.erp.core.repository.ClienteRepository;
import com.erp.core.service.ClienteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;

    public ClienteServiceImpl(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public ClienteResponse create(ClienteRequest request) {
        Cliente entity = new Cliente();
        entity.setNome(request.nome());
        entity.setDocumento(request.documento());
        entity.setEmail(request.email());
        entity.setTelefone(request.telefone());
        return mapToResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> getAll() {
        return repository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse getById(UUID id) {
        return repository.findById(id).map(this::mapToResponse).orElseThrow(() -> new IllegalArgumentException("Not found"));
    }

    @Override
    @Transactional
    public ClienteResponse update(UUID id, ClienteRequest request) {
        Cliente entity = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Not found"));
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

    private ClienteResponse mapToResponse(Cliente entity) {
        return new ClienteResponse(
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
