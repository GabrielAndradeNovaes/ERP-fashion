package com.erp.core.tenant.controller;

import com.erp.core.tenant.TenantProvisioningService;
import com.erp.core.tenant.dto.TenantProvisionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminTenantControllerTest {

    @Mock
    private TenantProvisioningService service;

    @Mock
    private JdbcTemplate jdbcTemplate;

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
        
        verify(service).startProvisioning("Empresa", "schema", "Admin", "admin@a.com", "123");
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
        
        verify(service).startProvisioning(eq("Empresa"), anyString(), eq("Admin"), eq("admin@a.com"), eq("123"));
        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
    }

    @Test
    void testListTenants() {
        List<Map<String, Object>> list = Collections.singletonList(Collections.singletonMap("key", "value"));
        when(jdbcTemplate.queryForList(anyString())).thenReturn(list);
        ResponseEntity<List<Map<String, Object>>> result = controller.listTenants();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(list, result.getBody());
    }

    @Test
    void testUpdateStatus() {
        Map<String, String> payload = Collections.singletonMap("status", "ATIVO");
        ResponseEntity<String> result = controller.updateStatus("schema", payload);
        verify(jdbcTemplate).update(anyString(), eq("ATIVO"), eq("schema"));
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
