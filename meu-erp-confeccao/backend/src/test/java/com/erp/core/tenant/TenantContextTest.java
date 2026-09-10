package com.erp.core.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldReturnMasterTenantAsDefault() {
        assertEquals("master", TenantContext.getCurrentTenant());
    }

    @Test
    void shouldSetAndReturnCurrentTenant() {
        TenantContext.setCurrentTenant("tenant_2");
        assertEquals("tenant_2", TenantContext.getCurrentTenant());
    }

    @Test
    void shouldClearTenantContext() {
        TenantContext.setCurrentTenant("tenant_3");
        TenantContext.clear();
        assertEquals("master", TenantContext.getCurrentTenant());
    }
}
