package com.erp.core.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FlywayConfigurationTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private FlywayConfiguration flywayConfiguration;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(flywayConfiguration, "url", "jdbc:h2:mem:testdb");
        ReflectionTestUtils.setField(flywayConfiguration, "username", "sa");
        ReflectionTestUtils.setField(flywayConfiguration, "password", "");
        
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
    }

    @Test
    void shouldGetTenantsSuccessfully() throws Exception {
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("schema_name")).thenReturn("tenant_1", "tenant_2");

        List<String> tenants = ReflectionTestUtils.invokeMethod(flywayConfiguration, "getTenants", dataSource);

        assertEquals(2, tenants.size());
        assertTrue(tenants.contains("tenant_1"));
        assertTrue(tenants.contains("tenant_2"));
    }

    @Test
    void shouldHandleExceptionWhenGettingTenants() throws Exception {
        when(dataSource.getConnection()).thenThrow(new RuntimeException("DB Connection Error"));

        List<String> tenants = ReflectionTestUtils.invokeMethod(flywayConfiguration, "getTenants", dataSource);

        assertEquals(1, tenants.size()); // Hardcoded tenant_1 is always added
        assertTrue(tenants.contains("tenant_1"));
    }
}
