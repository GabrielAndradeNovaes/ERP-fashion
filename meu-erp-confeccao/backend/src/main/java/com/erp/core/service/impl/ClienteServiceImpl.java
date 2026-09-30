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
    private final jakarta.persistence.EntityManager entityManager;

    public ClienteServiceImpl(ClienteRepository repository, jakarta.persistence.EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public ClienteResponse create(ClienteRequest request) {
        Cliente entity = new Cliente();
        entity.setNome(request.nome());
        entity.setDocumento(request.documento());
        entity.setEmail(request.email());
        entity.setTelefone(request.telefone());
        entity.setTipoPessoa(request.tipoPessoa());
        entity.setRazaoSocial(request.razaoSocial());
        entity.setInscricaoEstadual(request.inscricaoEstadual());
        entity.setLimiteCredito(request.limiteCredito());
        entity.setTabelaPrecoPadrao(request.tabelaPrecoPadrao());
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
        entity.setTipoPessoa(request.tipoPessoa());
        entity.setRazaoSocial(request.razaoSocial());
        entity.setInscricaoEstadual(request.inscricaoEstadual());
        entity.setLimiteCredito(request.limiteCredito());
        entity.setTabelaPrecoPadrao(request.tabelaPrecoPadrao());
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

    private ClienteResponse mapToResponse(Cliente entity) {
        return new ClienteResponse(
                entity.getId(),
                entity.getNome(),
                entity.getDocumento(),
                entity.getEmail(),
                entity.getTelefone(),
                entity.getTipoPessoa(),
                entity.getRazaoSocial(),
                entity.getInscricaoEstadual(),
                entity.getLimiteCredito(),
                entity.getTabelaPrecoPadrao(),
                entity.getStatus(),
                entity.getEndereco()
        );
    }
}
