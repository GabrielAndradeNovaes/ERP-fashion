package com.erp.core.tenant;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TenantDataSourceConfiguration implements WebMvcConfigurer {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    private final TenantInterceptor tenantInterceptor;

    public TenantDataSourceConfiguration(TenantInterceptor tenantInterceptor) {
        this.tenantInterceptor = tenantInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor);
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        // Configurando o datasource do schema MASTER
        DataSource masterDataSource = createDataSource("master");

        TenantRoutingDataSource customDataSource = new TenantRoutingDataSource(
            masterDataSource, url, username, password, driverClassName
        );
        
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(TenantContext.MASTER_TENANT, masterDataSource);

        customDataSource.setTargetDataSources(targetDataSources);
        customDataSource.setDefaultTargetDataSource(masterDataSource);
        customDataSource.afterPropertiesSet();

        return customDataSource;
    }

    private DataSource createDataSource(String schema) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        // Utilizando currentSchema do PostgreSQL
        dataSource.setSchema(schema);
        return dataSource;
    }
}
