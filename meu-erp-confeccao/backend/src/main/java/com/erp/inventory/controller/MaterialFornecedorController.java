package com.erp.inventory.controller;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Fornecedor;
import com.erp.core.repository.EmpresaRepository;
import com.erp.core.repository.FornecedorRepository;
import com.erp.inventory.domain.Material;
import com.erp.inventory.domain.MaterialFornecedor;
import com.erp.inventory.repository.MaterialFornecedorRepository;
import com.erp.inventory.repository.MaterialRepository;
import com.erp.core.tenant.EmpresaContext;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory/materiais-fornecedor")
public class MaterialFornecedorController {

    private final MaterialFornecedorRepository materialFornecedorRepository;
    private final FornecedorRepository fornecedorRepository;
    private final MaterialRepository materialRepository;
    private final EmpresaRepository empresaRepository;

    public MaterialFornecedorController(
            MaterialFornecedorRepository materialFornecedorRepository,
            FornecedorRepository fornecedorRepository,
            MaterialRepository materialRepository,
            EmpresaRepository empresaRepository) {
        this.materialFornecedorRepository = materialFornecedorRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.materialRepository = materialRepository;
        this.empresaRepository = empresaRepository;
    }

    @PostMapping("/vincular")
    @Transactional
    public ResponseEntity<?> vincularDePara(@RequestBody Map<String, Object> payload) {
        try {
            UUID fornecedorId = UUID.fromString(payload.get("fornecedorId").toString());
            UUID materialId = UUID.fromString(payload.get("materialId").toString());
            String codigoFornecedor = payload.get("codigoFornecedor").toString();
            String descricaoFornecedor = payload.get("descricaoFornecedor") != null ? payload.get("descricaoFornecedor").toString() : null;
            
            BigDecimal fatorConversao = BigDecimal.ONE;
            if (payload.containsKey("fatorConversao") && payload.get("fatorConversao") != null) {
                fatorConversao = new BigDecimal(payload.get("fatorConversao").toString());
            }

            Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId)
                    .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado"));
                    
            Material material = materialRepository.findById(materialId)
                    .orElseThrow(() -> new RuntimeException("Material interno não encontrado"));

            List<UUID> empresas = EmpresaContext.getEmpresas();
            Empresa empresa;
            if (empresas != null && !empresas.isEmpty()) {
                empresa = empresaRepository.findById(empresas.get(0))
                        .orElseThrow(() -> new RuntimeException("Empresa não encontrada no banco"));
            } else {
                empresa = empresaRepository.findAll().get(0);
            }

            // Verifica se já existe um mapeamento
            MaterialFornecedor mapping = materialFornecedorRepository
                    .findByFornecedorIdAndCodigoFornecedor(fornecedorId, codigoFornecedor)
                    .orElse(new MaterialFornecedor());

            mapping.setEmpresa(empresa);
            mapping.setFornecedor(fornecedor);
            mapping.setMaterial(material);
            mapping.setCodigoFornecedor(codigoFornecedor);
            mapping.setDescricaoFornecedor(descricaoFornecedor);
            mapping.setFatorConversao(fatorConversao);

            materialFornecedorRepository.save(mapping);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Mapeamento De/Para realizado com sucesso",
                "mappingId", mapping.getId()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Erro ao vincular"));
        }
    }
}
