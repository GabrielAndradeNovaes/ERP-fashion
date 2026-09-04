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
    public ResponseEntity<java.util.Map<String, Object>> getDashboardMetrics() {
        long totalTenants = tenantRepository.count();
        long activeTenants = tenantRepository.countByStatus("ATIVO");
        long inactiveTenants = tenantRepository.countByStatus("INATIVO") + tenantRepository.countByStatus("INADIMPLENTE");
        long pendingTenants = tenantRepository.countByStatus("PENDENTE") + tenantRepository.countByStatus("CRIANDO_INFRA");
        
        double estimatedMRR = activeTenants * 499.90;

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        
        // Métricas básicas
        java.util.Map<String, Object> basicMetrics = new java.util.HashMap<>();
        basicMetrics.put("totalTenants", totalTenants);
        basicMetrics.put("activeTenants", activeTenants);
        basicMetrics.put("inactiveTenants", inactiveTenants);
        basicMetrics.put("pendingTenants", pendingTenants);
        basicMetrics.put("estimatedMRR", estimatedMRR);
        response.put("metrics", basicMetrics);

        // Mock de Gráfico de MRR (últimos 6 meses)
        java.util.List<java.util.Map<String, Object>> mrrHistory = java.util.Arrays.asList(
            java.util.Map.of("name", "Mar", "mrr", estimatedMRR * 0.6),
            java.util.Map.of("name", "Abr", "mrr", estimatedMRR * 0.7),
            java.util.Map.of("name", "Mai", "mrr", estimatedMRR * 0.75),
            java.util.Map.of("name", "Jun", "mrr", estimatedMRR * 0.85),
            java.util.Map.of("name", "Jul", "mrr", estimatedMRR * 0.95),
            java.util.Map.of("name", "Ago", "mrr", estimatedMRR)
        );
        response.put("mrrHistory", mrrHistory);

        // Mock de Gráfico de Novos Tenants (últimos 6 meses)
        java.util.List<java.util.Map<String, Object>> tenantSignups = java.util.Arrays.asList(
            java.util.Map.of("name", "Mar", "novos", 2),
            java.util.Map.of("name", "Abr", "novos", 4),
            java.util.Map.of("name", "Mai", "novos", 3),
            java.util.Map.of("name", "Jun", "novos", 7),
            java.util.Map.of("name", "Jul", "novos", 5),
            java.util.Map.of("name", "Ago", "novos", 8)
        );
        response.put("tenantSignups", tenantSignups);

        // Mock de Saúde do Sistema
        java.util.Map<String, Object> systemHealth = java.util.Map.of(
            "uptime", "99.98%",
            "cpuUsage", 34,
            "memoryUsage", 62,
            "dbLatency", "12ms"
        );
        response.put("systemHealth", systemHealth);

        // Mock de Atividades Recentes
        java.util.List<java.util.Map<String, Object>> recentActivities = java.util.Arrays.asList(
            java.util.Map.of("id", 1, "title", "Novo cliente cadastrado", "description", "Empresa ABC Ltda provisionada no schema tenant_abc", "time", "Há 2 horas", "type", "success"),
            java.util.Map.of("id", 2, "title", "Alerta de Inadimplência", "description", "Tenant XYZ atrasou o pagamento", "time", "Há 5 horas", "type", "warning"),
            java.util.Map.of("id", 3, "title", "Módulos atualizados", "description", "Módulo PCP ativado para Confecções Style", "time", "Há 1 dia", "type", "info"),
            java.util.Map.of("id", 4, "title", "Backup Global concluído", "description", "Rotina de backup finalizada com sucesso", "time", "Há 1 dia", "type", "success")
        );
        response.put("recentActivities", recentActivities);

        return ResponseEntity.ok(response);
    }
}
