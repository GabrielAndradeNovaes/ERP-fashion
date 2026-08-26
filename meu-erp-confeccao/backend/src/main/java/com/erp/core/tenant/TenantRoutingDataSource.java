package com.erp.core.tenant;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    private final Map<String, DataSource> dynamicDataSources = new ConcurrentHashMap<>();
    private final DataSource masterDataSource;
    
    private final String url;
    private final String username;
    private final String password;
    private final String driverClassName;

    public TenantRoutingDataSource(DataSource masterDataSource, String url, String username, String password, String driverClassName) {
        this.masterDataSource = masterDataSource;
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
    }

    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getCurrentTenant();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        String tenantId = (String) determineCurrentLookupKey();

        if (tenantId == null || TenantContext.MASTER_TENANT.equals(tenantId)) {
            return masterDataSource;
        }

        return dynamicDataSources.computeIfAbsent(tenantId, this::createAndRegisterDataSource);
    }

    private DataSource createAndRegisterDataSource(String tenantId) {
        // Query master db to check if tenant is valid
        if (!isTenantValidAndActive(tenantId)) {
            throw new IllegalArgumentException("Invalid or inactive tenant: " + tenantId);
        }

        // Se for válido, usamos o próprio tenantId (que é o schema_name)
        return createDataSource(tenantId);
    }

    private boolean isTenantValidAndActive(String schemaName) {
        String sql = "SELECT status FROM master.clientes_tenant WHERE schema_name = ?";
        try (Connection conn = masterDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, schemaName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    return "ATIVO".equals(status) || "INADIMPLENTE".equals(status);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validating tenant: " + schemaName, e);
        }
        return false;
    }

    private DataSource createDataSource(String schema) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setSchema(schema);
        return dataSource;
    }
}
