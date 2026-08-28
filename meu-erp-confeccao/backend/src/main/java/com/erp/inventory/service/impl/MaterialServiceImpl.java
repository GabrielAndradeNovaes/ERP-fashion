package com.erp.inventory.service.impl;

import com.erp.inventory.domain.Material;
import com.erp.inventory.dto.MaterialRequest;
import com.erp.inventory.dto.MaterialResponse;
import com.erp.inventory.repository.MaterialRepository;
import com.erp.inventory.service.MaterialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialServiceImpl(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    @Transactional
    public MaterialResponse createMaterial(MaterialRequest request) {
        if (materialRepository.findByCodigo(request.codigo()).isPresent()) {
            throw new IllegalArgumentException("Material com código " + request.codigo() + " já existe.");
        }

        Material material = new Material();
        material.setCodigo(request.codigo());
        material.setNome(request.nome());
        material.setDescricao(request.descricao());
        material.setUnidadeMedida(request.unidadeMedida());
        material.setCustoUnitario(request.custoUnitario() != null ? request.custoUnitario() : java.math.BigDecimal.ZERO);
        material.setTipoMaterial(request.tipoMaterial());
        material.setComposicao(request.composicao());
        material.setNcm(request.ncm());
        material.setUnidadeCompra(request.unidadeCompra());
        material.setFatorConversao(request.fatorConversao());
        material.setLargura(request.largura());
        material.setGramatura(request.gramatura());
        material.setRendimento(request.rendimento());
        material.setStatus(request.status());
        material.setFornecedorPadraoId(request.fornecedorPadraoId());

        Material saved = materialRepository.save(material);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaterialResponse> getAllMateriais() {
        return materialRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MaterialResponse getMaterialById(UUID id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Material não encontrado."));
        return mapToResponse(material);
    }

    private MaterialResponse mapToResponse(Material material) {
        return new MaterialResponse(
                material.getId(),
                material.getCodigo(),
                material.getNome(),
                material.getDescricao(),
                material.getUnidadeMedida(),
                material.getCustoUnitario(),
                material.getQuantidadeAtual(),
                material.getTipoMaterial(),
                material.getComposicao(),
                material.getNcm(),
                material.getUnidadeCompra(),
                material.getFatorConversao(),
                material.getLargura(),
                material.getGramatura(),
                material.getRendimento(),
                material.getStatus(),
                material.getFornecedorPadraoId()
        );
    }
}
