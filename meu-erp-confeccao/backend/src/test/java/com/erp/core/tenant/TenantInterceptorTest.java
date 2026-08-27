package com.erp.core.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TenantInterceptorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TenantInterceptor interceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        TenantContext.clear();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void preHandle_WithTenantHeader_SetsTenant() throws Exception {
        when(request.getHeader("X-TenantID")).thenReturn("custom_tenant");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals("custom_tenant", TenantContext.getCurrentTenant());
    }

    @Test
    void preHandle_WithoutTenantHeader_SetsMasterTenant() throws Exception {
        when(request.getHeader("X-TenantID")).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals(TenantContext.MASTER_TENANT, TenantContext.getCurrentTenant());
    }

    @Test
    void afterCompletion_ClearsTenant() throws Exception {
        TenantContext.setCurrentTenant("some_tenant");

        interceptor.afterCompletion(request, response, new Object(), null);

        assertEquals(TenantContext.MASTER_TENANT, TenantContext.getCurrentTenant());
    }
}
