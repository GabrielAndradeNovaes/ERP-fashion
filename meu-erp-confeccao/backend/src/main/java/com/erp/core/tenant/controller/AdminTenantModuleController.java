package com.erp.core.tenant.controller;

import com.erp.core.tenant.TenantModule;
import com.erp.core.tenant.TenantModuleRepository;
import com.erp.core.tenant.dto.TenantModuleDTO;
import com.erp.core.tenant.dto.UpdateModulesRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/tenants/{tenantId}/modules")
public class AdminTenantModuleController {

    private final TenantModuleRepository tenantModuleRepository;
    
    // Todos os módulos possíveis
    private static final List<String> ALL_MODULES = Arrays.asList("CORE", "PCP", "ESTOQUE", "FINANCEIRO", "VENDAS", "CADASTROS");

    public AdminTenantModuleController(TenantModuleRepository tenantModuleRepository) {
        this.tenantModuleRepository = tenantModuleRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<TenantModuleDTO>> getTenantModules(@PathVariable String tenantId) {
        List<TenantModule> savedModules = tenantModuleRepository.findByTenantId(tenantId);
        
        // Mapeia para retornar todos os módulos possíveis. Se não estiver salvo, considera inativo (exceto CORE que é sempre true)
        List<TenantModuleDTO> result = ALL_MODULES.stream().map(moduleName -> {
            boolean isActive = savedModules.stream()
                    .filter(m -> m.getModuleName().equals(moduleName))
                    .map(TenantModule::isActive)
                    .findFirst()
                    .orElse(moduleName.equals("CORE")); // CORE é ativo por padrão se não houver registro
            return new TenantModuleDTO(moduleName, isActive);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> updateTenantModules(@PathVariable String tenantId, @RequestBody UpdateModulesRequestDTO request) {
        List<TenantModule> savedModules = tenantModuleRepository.findByTenantId(tenantId);

        for (TenantModuleDTO dto : request.getModules()) {
            TenantModule existing = savedModules.stream()
                    .filter(m -> m.getModuleName().equals(dto.getModuleName()))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                // Não permite desativar o CORE
                if (existing.getModuleName().equals("CORE")) {
                    existing.setActive(true);
                } else {
                    existing.setActive(dto.isActive());
                }
                tenantModuleRepository.save(existing);
            } else {
                TenantModule newModule = new TenantModule(tenantId, dto.getModuleName(), dto.isActive());
                // Força true para CORE
                if (dto.getModuleName().equals("CORE")) {
                    newModule.setActive(true);
                }
                tenantModuleRepository.save(newModule);
            }
        }
        return ResponseEntity.ok().build();
    }
}
