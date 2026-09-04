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

    @InjectMocks
    private AdminDashboardController controller;

    @Test
    void shouldGetMetrics() {
        when(tenantRepository.count()).thenReturn(10L);
        when(tenantRepository.countByStatus("ATIVO")).thenReturn(8L);
        when(tenantRepository.countByStatus("INATIVO")).thenReturn(1L);
        when(tenantRepository.countByStatus("INADIMPLENTE")).thenReturn(1L);
        when(tenantRepository.countByStatus("PENDENTE")).thenReturn(0L);
        when(tenantRepository.countByStatus("CRIANDO_INFRA")).thenReturn(0L);

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
