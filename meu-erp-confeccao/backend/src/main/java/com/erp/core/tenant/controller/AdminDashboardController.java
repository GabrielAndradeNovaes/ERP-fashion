package com.erp.core.tenant.controller;

import com.erp.core.tenant.TenantRepository;
import com.erp.core.tenant.dto.AdminDashboardMetricsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final TenantRepository tenantRepository;

    public AdminDashboardController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @GetMapping("/metrics")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<AdminDashboardMetricsDTO> getDashboardMetrics() {
        long totalTenants = tenantRepository.count();
        long activeTenants = tenantRepository.countByStatus("ATIVO");
        long inactiveTenants = tenantRepository.countByStatus("INATIVO") + tenantRepository.countByStatus("INADIMPLENTE");
        long pendingTenants = tenantRepository.countByStatus("PENDENTE") + tenantRepository.countByStatus("CRIANDO_INFRA");
        
        // Exemplo simples de MRR: R$ 499,90 por tenant ativo (você pode ajustar depois)
        double estimatedMRR = activeTenants * 499.90;

        AdminDashboardMetricsDTO metrics = new AdminDashboardMetricsDTO(
                totalTenants,
                activeTenants,
                inactiveTenants,
                pendingTenants,
                estimatedMRR
        );

        return ResponseEntity.ok(metrics);
    }
}
