package com.erp.core.tenant.controller;

import com.erp.core.tenant.TenantModule;
import com.erp.core.tenant.dto.TenantModuleDTO;
import com.erp.core.tenant.dto.UpdateModulesRequestDTO;
import com.erp.core.tenant.TenantModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminTenantModuleControllerTest {

    @Mock
    private TenantModuleRepository tenantModuleRepository;

    @InjectMocks
    private AdminTenantModuleController controller;

    private TenantModule moduleMock;

    @BeforeEach
    void setUp() {
        moduleMock = new TenantModule();
        moduleMock.setTenantId("tenant_123");
        moduleMock.setModuleName("PRODUCAO");
        moduleMock.setActive(true);
    }

    @Test
    void shouldGetTenantModules() {
        when(tenantModuleRepository.findByTenantId("tenant_123")).thenReturn(List.of(moduleMock));

        ResponseEntity<List<TenantModuleDTO>> response = controller.getTenantModules("tenant_123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    void shouldUpdateTenantModules() {
        when(tenantModuleRepository.findByTenantId("tenant_123")).thenReturn(List.of(moduleMock));
        when(tenantModuleRepository.save(any())).thenReturn(moduleMock);

        UpdateModulesRequestDTO req = new UpdateModulesRequestDTO();
        req.setModules(List.of(
                new TenantModuleDTO("PRODUCAO", false),
                new TenantModuleDTO("CORE", false) // CORE não pode ser desativado, o controller deve forçar true
        ));

        ResponseEntity<Void> response = controller.updateTenantModules("tenant_123", req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(tenantModuleRepository, times(2)).save(any(TenantModule.class));
    }
}
