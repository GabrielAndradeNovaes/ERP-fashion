package com.erp.core.tenant.controller;

import com.erp.core.tenant.Tenant;
import com.erp.core.tenant.TenantRepository;
import com.erp.core.tenant.TenantProvisioningService;
import com.erp.core.tenant.dto.TenantProvisionRequest;
import com.erp.core.tenant.dto.TenantResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminTenantControllerTest {

    @Mock
    private TenantProvisioningService service;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private AdminTenantController controller;

    @Test
    void testProvisionTenant() {
        TenantProvisionRequest req = new TenantProvisionRequest();
        req.setNomeEmpresa("Empresa");
        req.setSchemaName("schema");
        req.setAdminNome("Admin");
        req.setAdminEmail("admin@a.com");
        req.setAdminSenha("123");

        ResponseEntity<String> result = controller.provisionTenant(req);
        
        verify(service).startProvisioning(req);
        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
        assertTrue(result.getBody().contains("schema"));
    }

    @Test
    void testProvisionTenant_NoSchema() {
        TenantProvisionRequest req = new TenantProvisionRequest();
        req.setNomeEmpresa("Empresa");
        req.setAdminNome("Admin");
        req.setAdminEmail("admin@a.com");
        req.setAdminSenha("123");

        ResponseEntity<String> result = controller.provisionTenant(req);
        
        verify(service).startProvisioning(req);
        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
    }

    @Test
    void testListTenants() {
        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenant.setNomeEmpresa("Exemplo");
        tenant.setSchemaName("tenant_exemplo");
        tenant.setCriadoEm(LocalDateTime.now());
        
        List<Tenant> list = Collections.singletonList(tenant);
        when(tenantRepository.findAllByOrderByCriadoEmDesc()).thenReturn(list);
        
        ResponseEntity<List<TenantResponse>> result = controller.listTenants();
        
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("tenant_exemplo", result.getBody().get(0).getSchemaName());
    }

    @Test
    void testUpdateStatus() {
        Map<String, String> payload = Collections.singletonMap("status", "ATIVO");
        Tenant tenant = new Tenant();
        when(tenantRepository.findBySchemaName("schema")).thenReturn(tenant);
        
        ResponseEntity<String> result = controller.updateStatus("schema", payload);
        
        verify(tenantRepository).save(tenant);
        assertEquals("ATIVO", tenant.getStatus());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
