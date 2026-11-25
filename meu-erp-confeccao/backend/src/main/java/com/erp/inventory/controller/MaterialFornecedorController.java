package com.erp.inventory.controller;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Fornecedor;
import com.erp.core.repository.EmpresaRepository;
import com.erp.core.repository.FornecedorRepository;
import com.erp.core.tenant.EmpresaContext;
import com.erp.inventory.domain.Material;
import com.erp.inventory.domain.MaterialFornecedor;
import com.erp.inventory.repository.MaterialFornecedorRepository;
import com.erp.inventory.repository.MaterialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(MaterialFornecedorController.class);

    public record VincularMaterialFornecedorRequest(
            UUID fornecedorId,
            UUID materialId,
            String codigoFornecedor,
            String descricaoFornecedor,
            BigDecimal fatorConversao
    ) {}

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
    public ResponseEntity<?> vincularDePara(@RequestBody VincularMaterialFornecedorRequest request) {
        try {
            if (request == null || request.fornecedorId() == null || request.materialId() == null
                    || request.codigoFornecedor() == null || request.codigoFornecedor().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "fornecedorId, materialId e codigoFornecedor são obrigatórios."));
            }

            BigDecimal fatorConversao = request.fatorConversao() != null ? request.fatorConversao() : BigDecimal.ONE;
            if (fatorConversao.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "O fator de conversão deve ser maior que zero."));
            }

            Fornecedor fornecedor = fornecedorRepository.findById(request.fornecedorId())
                    .orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado"));

            Material material = materialRepository.findById(request.materialId())
                    .orElseThrow(() -> new IllegalArgumentException("Material interno não encontrado"));

            List<UUID> empresas = EmpresaContext.getEmpresas();
            Empresa empresa;
            if (empresas != null && !empresas.isEmpty()) {
                empresa = empresaRepository.findById(empresas.get(0))
                        .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada no banco"));
            } else {
                List<Empresa> todasEmpresas = empresaRepository.findAll();
                if (todasEmpresas.isEmpty()) {
                    throw new IllegalArgumentException("Nenhuma empresa cadastrada no tenant.");
                }
                empresa = todasEmpresas.get(0);
            }

            String codigoFornecedor = request.codigoFornecedor().trim();

            MaterialFornecedor mapping = materialFornecedorRepository
                    .findByFornecedorIdAndCodigoFornecedor(request.fornecedorId(), codigoFornecedor)
                    .orElse(new MaterialFornecedor());

            mapping.setEmpresa(empresa);
            mapping.setFornecedor(fornecedor);
            mapping.setMaterial(material);
            mapping.setCodigoFornecedor(codigoFornecedor);
            mapping.setDescricaoFornecedor(request.descricaoFornecedor());
            mapping.setFatorConversao(fatorConversao);

            mapping = materialFornecedorRepository.save(mapping);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Mapeamento De/Para realizado com sucesso",
                    "mappingId", mapping.getId(),
                    "fatorConversao", mapping.getFatorConversao()
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Erro de validação ao vincular material do fornecedor: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Erro inesperado ao vincular material do fornecedor", e);
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Erro ao vincular"));
        }
    }
}
