package com.erp.core.db;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FlywayConfiguration {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public boolean migrateFlyway(DataSource dataSource) {
        // 1. Migrar o schema Master
        Flyway flywayMaster = Flyway.configure()
                .dataSource(dataSource)
                .schemas("master")
                .locations("classpath:db/migration/master")
                .baselineOnMigrate(true)
                .load();
        flywayMaster.migrate();

        // 2. Buscar tenants configurados (a partir da tabela clientes_tenant no schema master)
        List<String> tenants = getTenants(dataSource);

        // 3. Migrar o schema de cada Tenant
        for (String tenant : tenants) {
            Flyway flywayTenant = Flyway.configure()
                    .dataSource(dataSource)
                    .schemas(tenant)
                    .locations("classpath:db/migration/tenant")
                    .baselineOnMigrate(true)
                    .load();
            flywayTenant.migrate();
        }

        return true;
    }

    private List<String> getTenants(DataSource dataSource) {
        List<String> tenants = new ArrayList<>();
        // Temporariamente adicionando tenant_1 hardcoded para garantir que roda,
        // mas idealmente faríamos um SELECT no banco master.
        tenants.add("tenant_1");
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            // Utilizamos IF EXISTS ou tratamos exception caso a tabela ainda não tenha registros
            ResultSet rs = stmt.executeQuery("SELECT schema_name FROM master.clientes_tenant");
            while (rs.next()) {
                String schema = rs.getString("schema_name");
                if (schema != null && !schema.equals("tenant_1") && !schema.equals("master")) {
                    tenants.add(schema);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar tenants (tabela pode estar vazia ou erro de sintaxe): " + e.getMessage());
        }
        return tenants;
    }
}
