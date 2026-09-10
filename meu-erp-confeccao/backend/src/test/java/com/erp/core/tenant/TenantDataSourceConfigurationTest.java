package com.erp.core.tenant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TenantDataSourceConfigurationTest {

    @Mock
    private TenantInterceptor tenantInterceptor;

    @InjectMocks
    private TenantDataSourceConfiguration configuration;

    @Test
    void testAddInterceptors() {
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        configuration.addInterceptors(registry);
        verify(registry).addInterceptor(tenantInterceptor);
    }

    @Test
    void testDataSource() {
        ReflectionTestUtils.setField(configuration, "url", "jdbc:postgresql://localhost:5432/db");
        ReflectionTestUtils.setField(configuration, "username", "user");
        ReflectionTestUtils.setField(configuration, "password", "pass");
        ReflectionTestUtils.setField(configuration, "driverClassName", "org.postgresql.Driver");

        DataSource dataSource = configuration.dataSource();

        assertNotNull(dataSource);
        assertTrue(dataSource instanceof TenantRoutingDataSource);
    }
}
