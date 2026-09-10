package com.erp.core.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TenantRoutingDataSourceTest {

    @Mock
    private DataSource masterDataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private TenantRoutingDataSource tenantRoutingDataSource;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        tenantRoutingDataSource = new TenantRoutingDataSource(
                masterDataSource,
                "jdbc:postgresql://localhost:5432/test",
                "sa",
                "",
                "org.postgresql.Driver"
        );

        when(masterDataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        
        TenantContext.clear();
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void shouldReturnMasterDataSourceWhenTenantIsMaster() {
        TenantContext.setCurrentTenant(TenantContext.MASTER_TENANT);

        DataSource resolvedDataSource = tenantRoutingDataSource.determineTargetDataSource();

        assertEquals(masterDataSource, resolvedDataSource);
    }

    @Test
    void shouldReturnMasterDataSourceWhenTenantIsNull() {
        TenantContext.setCurrentTenant(null);

        DataSource resolvedDataSource = tenantRoutingDataSource.determineTargetDataSource();

        assertEquals(masterDataSource, resolvedDataSource);
    }

    @Test
    void shouldCreateNewDataSourceWhenTenantIsActive() throws SQLException {
        TenantContext.setCurrentTenant("tenant_123");

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("status")).thenReturn("ATIVO");

        DataSource resolvedDataSource = tenantRoutingDataSource.determineTargetDataSource();

        assertNotNull(resolvedDataSource);
        assertNotEquals(masterDataSource, resolvedDataSource);
    }

    @Test
    void shouldThrowExceptionWhenTenantIsInactive() throws SQLException {
        TenantContext.setCurrentTenant("tenant_123");

        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("status")).thenReturn("INATIVO");

        assertThrows(IllegalArgumentException.class, () -> {
            tenantRoutingDataSource.determineTargetDataSource();
        });
    }

    @Test
    void shouldThrowExceptionWhenTenantNotFound() throws SQLException {
        TenantContext.setCurrentTenant("tenant_123");

        when(resultSet.next()).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            tenantRoutingDataSource.determineTargetDataSource();
        });
    }

    @Test
    void shouldThrowExceptionOnSqlError() throws SQLException {
        TenantContext.setCurrentTenant("tenant_123");

        when(masterDataSource.getConnection()).thenThrow(new SQLException("DB Error"));

        assertThrows(RuntimeException.class, () -> {
            tenantRoutingDataSource.determineTargetDataSource();
        });
    }
}
