package com.erp.core.tenant.controller;

import com.erp.core.tenant.TenantRepository;
import com.erp.core.tenant.dto.AdminDashboardMetricsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminDashboardControllerTest {

    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private com.erp.core.billing.repository.FaturaSaaSRepository faturaSaaSRepository;
    @Mock
    private com.erp.core.tenant.AcessoLogRepository acessoLogRepository;

    @InjectMocks
    private AdminDashboardController controller;

    @Test
    void shouldGetMetrics() {
        java.util.List<com.erp.core.tenant.Tenant> mockTenants = new java.util.ArrayList<>();
        for (int i = 0; i < 8; i++) {
            com.erp.core.tenant.Tenant t = new com.erp.core.tenant.Tenant();
            t.setStatus("ATIVO");
            mockTenants.add(t);
        }
        com.erp.core.tenant.Tenant tInativo = new com.erp.core.tenant.Tenant();
        tInativo.setStatus("INATIVO");
        mockTenants.add(tInativo);
        com.erp.core.tenant.Tenant tInadimplente = new com.erp.core.tenant.Tenant();
        tInadimplente.setStatus("INADIMPLENTE");
        mockTenants.add(tInadimplente);

        when(tenantRepository.findAll()).thenReturn(mockTenants);

        ResponseEntity<java.util.Map<String, Object>> response = controller.getDashboardMetrics();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        java.util.Map<String, Object> metrics = (java.util.Map<String, Object>) response.getBody().get("metrics");
        assertNotNull(metrics);
        assertEquals(10L, metrics.get("totalTenants"));
        assertEquals(8L, metrics.get("activeTenants"));
        assertEquals(2L, metrics.get("inactiveTenants"));
        assertEquals(0L, metrics.get("pendingTenants"));
        assertEquals(8L * 499.90, (Double) metrics.get("estimatedMRR"), 0.01);
    }
}
