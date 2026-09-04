package com.erp.core.tenant.controller;

import com.erp.core.tenant.Tenant;
import com.erp.core.tenant.TenantProvisioningService;
import com.erp.core.tenant.dto.TenantProvisionRequest;
import com.erp.core.tenant.dto.TenantResponse;
import com.erp.core.tenant.dto.TenantUpdateRequest;
import com.erp.core.tenant.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminTenantControllerTest {

    @Mock
    private TenantProvisioningService tenantProvisioningService;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private AdminTenantController adminTenantController;

    private Tenant tenantMock;

    @BeforeEach
    void setUp() {
        tenantMock = new Tenant();
        tenantMock.setId(UUID.randomUUID());
        tenantMock.setNomeEmpresa("Tenant Teste");
        tenantMock.setSchemaName("tenant_123");
        tenantMock.setSlug("tenant-teste");
        tenantMock.setStatus("ATIVO");
        tenantMock.setCriadoEm(LocalDateTime.now());
    }

    @Test
    void shouldListAllTenants() {
        when(tenantRepository.findAllByOrderByCriadoEmDesc()).thenReturn(List.of(tenantMock));

        ResponseEntity<List<TenantResponse>> response = adminTenantController.listTenants();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldProvisionTenant() {
        TenantProvisionRequest req = new TenantProvisionRequest();
        req.setNomeEmpresa("Novo");
        req.setSchemaName("novo");

        ResponseEntity<String> response = adminTenantController.provisionTenant(req);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldUpdateTenant() {
        TenantUpdateRequest req = new TenantUpdateRequest();
        req.setNomeEmpresa("Novo Nome");

        when(tenantRepository.findBySchemaName("tenant_123")).thenReturn(tenantMock);
        when(tenantRepository.save(any())).thenReturn(tenantMock);

        ResponseEntity<TenantResponse> response = adminTenantController.updateTenant("tenant_123", req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Novo Nome", tenantMock.getNomeEmpresa());
    }

    @Test
    void shouldUpdateStatus() {
        when(tenantRepository.findBySchemaName("tenant_123")).thenReturn(tenantMock);
        when(tenantRepository.save(any())).thenReturn(tenantMock);

        ResponseEntity<String> response = adminTenantController.updateStatus("tenant_123", Map.of("status", "BLOQUEADO"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("BLOQUEADO", tenantMock.getStatus());
    }
}
