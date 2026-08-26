package com.erp.production.service.impl;

import com.erp.catalog.domain.ProdutoBase;
import com.erp.catalog.repository.ProdutoBaseRepository;
import com.erp.inventory.domain.Material;
import com.erp.inventory.repository.MaterialRepository;
import com.erp.production.domain.FichaTecnica;
import com.erp.production.domain.FichaTecnicaMaterial;
import com.erp.production.domain.FichaTecnicaOperacao;
import com.erp.production.domain.TabelaTempoPadrao;
import com.erp.production.dto.*;
import com.erp.production.repository.FichaTecnicaOperacaoRepository;
import com.erp.production.repository.FichaTecnicaRepository;
import com.erp.production.repository.TabelaTempoPadraoRepository;
import com.erp.production.service.FichaTecnicaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FichaTecnicaServiceImpl implements FichaTecnicaService {

    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final ProdutoBaseRepository produtoBaseRepository;
    private final MaterialRepository materialRepository;
    private final TabelaTempoPadraoRepository tabelaTempoPadraoRepository;
    private final FichaTecnicaOperacaoRepository fichaTecnicaOperacaoRepository;

    public FichaTecnicaServiceImpl(FichaTecnicaRepository fichaTecnicaRepository, 
                                   ProdutoBaseRepository produtoBaseRepository, 
                                   MaterialRepository materialRepository,
                                   TabelaTempoPadraoRepository tabelaTempoPadraoRepository,
                                   FichaTecnicaOperacaoRepository fichaTecnicaOperacaoRepository) {
        this.fichaTecnicaRepository = fichaTecnicaRepository;
        this.produtoBaseRepository = produtoBaseRepository;
        this.materialRepository = materialRepository;
        this.tabelaTempoPadraoRepository = tabelaTempoPadraoRepository;
        this.fichaTecnicaOperacaoRepository = fichaTecnicaOperacaoRepository;
    }

    @Override
    @Transactional
    public FichaTecnicaResponse createFichaTecnica(FichaTecnicaRequest request) {
        ProdutoBase produtoBase = produtoBaseRepository.findById(request.produtoBaseId())
                .orElseThrow(() -> new IllegalArgumentException("Produto Base não encontrado com ID: " + request.produtoBaseId()));

        FichaTecnica fichaTecnica = new FichaTecnica();
        fichaTecnica.setProdutoBase(produtoBase);
        fichaTecnica.setVersao(request.versao());
        fichaTecnica.setObservacoes(request.observacoes());

        if (request.materiais() != null) {
            for (FichaTecnicaMaterialRequest matReq : request.materiais()) {
                Material material = materialRepository.findById(matReq.materialId())
                        .orElseThrow(() -> new IllegalArgumentException("Material não encontrado com ID: " + matReq.materialId()));

                FichaTecnicaMaterial fctMaterial = new FichaTecnicaMaterial();
                fctMaterial.setMaterial(material);
                fctMaterial.setQuantidade(matReq.quantidade());
                
                fichaTecnica.addMaterial(fctMaterial);
            }
        }

        FichaTecnica saved = fichaTecnicaRepository.save(fichaTecnica);
        
        // Atualizar o precoCusto do ProdutoBase
        BigDecimal custoTotal = BigDecimal.ZERO;
        if (saved.getMateriais() != null) {
            for (FichaTecnicaMaterial mat : saved.getMateriais()) {
                BigDecimal custoUnit = mat.getMaterial().getCustoUnitario() != null ? mat.getMaterial().getCustoUnitario() : BigDecimal.ZERO;
                custoTotal = custoTotal.add(mat.getQuantidade().multiply(custoUnit));
            }
        }
        produtoBase.setPrecoCusto(custoTotal);
        produtoBaseRepository.save(produtoBase);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FichaTecnicaResponse> getFichasPorProduto(UUID produtoBaseId) {
        return fichaTecnicaRepository.findAll().stream()
                .filter(f -> f.getProdutoBase().getId().equals(produtoBaseId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FichaTecnicaResponse getFichaTecnicaById(UUID id) {
        FichaTecnica fichaTecnica = fichaTecnicaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ficha Técnica não encontrada com ID: " + id));
        return mapToResponse(fichaTecnica);
    }

    @Override
    @Transactional
    public FichaTecnicaResponse addOperacao(UUID fichaTecnicaId, FichaTecnicaOperacaoRequest request) {
        FichaTecnica fichaTecnica = fichaTecnicaRepository.findById(fichaTecnicaId)
                .orElseThrow(() -> new IllegalArgumentException("Ficha Técnica não encontrada com ID: " + fichaTecnicaId));

        Integer qFolhas = request.quantidadeFolhas() != null ? request.quantidadeFolhas() : 0;
        Integer qParadas = request.quantidadeParadas() != null ? request.quantidadeParadas() : 0;
        Integer indice = qFolhas + qParadas;

        TabelaTempoPadrao tempoPadrao = tabelaTempoPadraoRepository.findByIndiceAndGrauDificuldadeAndFaixaComprimento(
                indice, request.grauDificuldade(), request.faixaComprimento()
        ).orElse(null);

        FichaTecnicaOperacao operacao = new FichaTecnicaOperacao();
        operacao.setNome(request.nome());
        operacao.setMaquina(request.maquina());
        operacao.setOrdemExecucao(request.ordemExecucao());
        operacao.setQuantidadeFolhas(qFolhas);
        operacao.setQuantidadeParadas(qParadas);
        operacao.setGrauDificuldade(request.grauDificuldade());
        operacao.setFaixaComprimento(request.faixaComprimento());
        operacao.setTempoCalculadoCentesimal(tempoPadrao != null ? tempoPadrao.getTempoCentesimal() : BigDecimal.ZERO);

        fichaTecnica.addOperacao(operacao);
        
        FichaTecnica saved = fichaTecnicaRepository.save(fichaTecnica);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public FichaTecnicaResponse removeOperacao(UUID fichaTecnicaId, UUID operacaoId) {
        FichaTecnica fichaTecnica = fichaTecnicaRepository.findById(fichaTecnicaId)
                .orElseThrow(() -> new IllegalArgumentException("Ficha Técnica não encontrada com ID: " + fichaTecnicaId));

        FichaTecnicaOperacao operacao = fichaTecnicaOperacaoRepository.findById(operacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Operação não encontrada com ID: " + operacaoId));

        if (!operacao.getFichaTecnica().getId().equals(fichaTecnicaId)) {
            throw new IllegalArgumentException("A operação não pertence a esta Ficha Técnica.");
        }

        fichaTecnica.removeOperacao(operacao);
        FichaTecnica saved = fichaTecnicaRepository.save(fichaTecnica);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public FichaTecnicaResponse addMaterial(UUID fichaTecnicaId, com.erp.production.dto.FichaTecnicaMaterialRequest request) {
        FichaTecnica fichaTecnica = fichaTecnicaRepository.findById(fichaTecnicaId)
                .orElseThrow(() -> new IllegalArgumentException("Ficha Técnica não encontrada com ID: " + fichaTecnicaId));

        Material material = materialRepository.findById(request.materialId())
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado com ID: " + request.materialId()));

        FichaTecnicaMaterial fctMaterial = new FichaTecnicaMaterial();
        fctMaterial.setMaterial(material);
        fctMaterial.setQuantidade(request.quantidade());

        fichaTecnica.addMaterial(fctMaterial);
        
        FichaTecnica saved = fichaTecnicaRepository.save(fichaTecnica);
        
        // Atualizar precoCusto do ProdutoBase
        atualizarPrecoCustoProdutoBase(saved);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public FichaTecnicaResponse removeMaterial(UUID fichaTecnicaId, UUID materialId) {
        FichaTecnica fichaTecnica = fichaTecnicaRepository.findById(fichaTecnicaId)
                .orElseThrow(() -> new IllegalArgumentException("Ficha Técnica não encontrada com ID: " + fichaTecnicaId));

        FichaTecnicaMaterial materialToRemove = fichaTecnica.getMateriais().stream()
                .filter(m -> m.getId().equals(materialId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado na Ficha Técnica"));

        fichaTecnica.removeMaterial(materialToRemove);
        FichaTecnica saved = fichaTecnicaRepository.save(fichaTecnica);
        
        // Atualizar precoCusto do ProdutoBase
        atualizarPrecoCustoProdutoBase(saved);

        return mapToResponse(saved);
    }

    private void atualizarPrecoCustoProdutoBase(FichaTecnica ficha) {
        ProdutoBase produto = ficha.getProdutoBase();
        BigDecimal custoTotal = BigDecimal.ZERO;
        if (ficha.getMateriais() != null) {
            for (FichaTecnicaMaterial mat : ficha.getMateriais()) {
                BigDecimal custoUnit = mat.getMaterial().getCustoUnitario() != null ? mat.getMaterial().getCustoUnitario() : BigDecimal.ZERO;
                custoTotal = custoTotal.add(mat.getQuantidade().multiply(custoUnit));
            }
        }
        produto.setPrecoCusto(custoTotal);
        produtoBaseRepository.save(produto);
    }

    private FichaTecnicaResponse mapToResponse(FichaTecnica fichaTecnica) {
        List<FichaTecnicaMaterialResponse> materiais = new ArrayList<>();
        BigDecimal custoTotalMateriais = BigDecimal.ZERO;
        
        if (fichaTecnica.getMateriais() != null) {
            for (FichaTecnicaMaterial mat : fichaTecnica.getMateriais()) {
                BigDecimal custoUnit = mat.getMaterial().getCustoUnitario() != null ? mat.getMaterial().getCustoUnitario() : BigDecimal.ZERO;
                custoTotalMateriais = custoTotalMateriais.add(mat.getQuantidade().multiply(custoUnit));
                
                materiais.add(new FichaTecnicaMaterialResponse(
                        mat.getId(),
                        mat.getMaterial().getId(),
                        mat.getMaterial().getNome(),
                        mat.getMaterial().getUnidadeMedida(),
                        mat.getQuantidade()
                ));
            }
        }

        List<FichaTecnicaOperacaoResponse> operacoes = new ArrayList<>();
        if (fichaTecnica.getOperacoes() != null) {
            operacoes = fichaTecnica.getOperacoes().stream()
                    .map(op -> new FichaTecnicaOperacaoResponse(
                            op.getId(),
                            op.getNome(),
                            op.getMaquina(),
                            op.getOrdemExecucao(),
                            op.getQuantidadeFolhas(),
                            op.getQuantidadeParadas(),
                            op.getGrauDificuldade(),
                            op.getFaixaComprimento(),
                            op.getTempoCalculadoCentesimal()
                    )).collect(Collectors.toList());
        }

        BigDecimal tempoPadraoTotal = fichaTecnica.getTempoPadraoTotalCentesimal();
        if (tempoPadraoTotal == null) tempoPadraoTotal = BigDecimal.ZERO;

        return new FichaTecnicaResponse(
                fichaTecnica.getId(),
                fichaTecnica.getProdutoBase().getId(),
                fichaTecnica.getProdutoBase().getNome(),
                fichaTecnica.getVersao(),
                fichaTecnica.getObservacoes(),
                tempoPadraoTotal,
                custoTotalMateriais,
                materiais,
                operacoes
        );
    }
}
