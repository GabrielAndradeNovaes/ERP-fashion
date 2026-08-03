package com.erp.production.service.impl;

import com.erp.production.domain.TabelaTempoPadrao;
import com.erp.production.dto.TabelaTempoPadraoRequest;
import com.erp.production.dto.TabelaTempoPadraoResponse;
import com.erp.production.repository.TabelaTempoPadraoRepository;
import com.erp.production.service.TabelaTempoPadraoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TabelaTempoPadraoServiceImpl implements TabelaTempoPadraoService {

    private final TabelaTempoPadraoRepository repository;

    public TabelaTempoPadraoServiceImpl(TabelaTempoPadraoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public TabelaTempoPadraoResponse create(TabelaTempoPadraoRequest request) {
        validarUnicidade(request, null);

        TabelaTempoPadrao tabela = new TabelaTempoPadrao();
        tabela.setIndice(request.indice());
        tabela.setGrauDificuldade(request.grauDificuldade());
        tabela.setFaixaComprimento(request.faixaComprimento());
        tabela.setTempoCentesimal(request.tempoCentesimal());

        TabelaTempoPadrao saved = repository.save(tabela);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public TabelaTempoPadraoResponse update(UUID id, TabelaTempoPadraoRequest request) {
        TabelaTempoPadrao tabela = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tabela não encontrada."));

        validarUnicidade(request, id);

        tabela.setIndice(request.indice());
        tabela.setGrauDificuldade(request.grauDificuldade());
        tabela.setFaixaComprimento(request.faixaComprimento());
        tabela.setTempoCentesimal(request.tempoCentesimal());

        TabelaTempoPadrao updated = repository.save(tabela);
        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TabelaTempoPadraoResponse> getAll() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private void validarUnicidade(TabelaTempoPadraoRequest request, UUID idAtual) {
        repository.findByIndiceAndGrauDificuldadeAndFaixaComprimento(
                request.indice(), request.grauDificuldade(), request.faixaComprimento()
        ).ifPresent(existente -> {
            if (idAtual == null || !existente.getId().equals(idAtual)) {
                throw new IllegalArgumentException("Já existe um tempo padrão cadastrado para essa combinação de índice, dificuldade e comprimento.");
            }
        });
    }

    private TabelaTempoPadraoResponse mapToResponse(TabelaTempoPadrao tabela) {
        return new TabelaTempoPadraoResponse(
                tabela.getId(),
                tabela.getIndice(),
                tabela.getGrauDificuldade(),
                tabela.getFaixaComprimento(),
                tabela.getTempoCentesimal()
        );
    }
}
