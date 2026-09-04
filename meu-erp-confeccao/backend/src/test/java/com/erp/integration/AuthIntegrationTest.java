package com.erp.integration;

import com.erp.core.security.Usuario;
import com.erp.core.security.dto.AuthRequest;
import com.erp.core.security.dto.AuthResponse;
import com.erp.core.tenant.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws Exception {
        // Preparar banco de dados: 
        // 1. Inserir tenant na tabela master
        // 2. Inserir usuário na tabela master com tenant correto
        
        try (Connection conn = dataSource.getConnection()) {
            // Cria os schemas necessarios para o teste não quebrar se o Flyway não fez
            conn.createStatement().execute("CREATE SCHEMA IF NOT EXISTS master;");
            
            // Verifica se a tabela clientes_tenant existe (o Flyway deve ter criado, mas garantimos os dados)
            // Se o Flyway rodou, a tabela já existe. Limpamos para garantir estado.
            conn.createStatement().execute("TRUNCATE TABLE master.usuarios CASCADE;");
            conn.createStatement().execute("TRUNCATE TABLE master.clientes_tenant CASCADE;");

            // Insere Tenant 1 (Petrobras)
            PreparedStatement stmtTenant = conn.prepareStatement(
                    "INSERT INTO master.clientes_tenant (id, razao_social, cnpj, schema_name, slug, email_contato, telefone_contato, status) " +
                            "VALUES (?, 'Petrobras', '11111111111111', 'tenant_petrobras', 'petrobras', 'contato@petrobras.com', '111111', 'ATIVO')"
            );
            stmtTenant.setObject(1, UUID.randomUUID());
            stmtTenant.executeUpdate();

            // Insere Usuario para a Petrobras
            PreparedStatement stmtUser = conn.prepareStatement(
                    "INSERT INTO master.usuarios (id, nome, email, senha, tenant_id, role) " +
                            "VALUES (?, 'Admin Petrobras', 'admin@petrobras.com', ?, 'tenant_petrobras', 'ADMIN')"
            );
            stmtUser.setObject(1, UUID.randomUUID());
            stmtUser.setString(2, passwordEncoder.encode("senha123"));
            stmtUser.executeUpdate();
            
            // Insere Tenant 2 (Vale)
            PreparedStatement stmtTenant2 = conn.prepareStatement(
                    "INSERT INTO master.clientes_tenant (id, razao_social, cnpj, schema_name, slug, email_contato, telefone_contato, status) " +
                            "VALUES (?, 'Vale', '22222222222222', 'tenant_vale', 'vale', 'contato@vale.com', '222222', 'ATIVO')"
            );
            stmtTenant2.setObject(1, UUID.randomUUID());
            stmtTenant2.executeUpdate();
        }
    }

    @Test
    void testLoginComSucessoParaPetrobras() {
        AuthRequest request = new AuthRequest();
        request.setEmail("admin@petrobras.com");
        request.setSenha("senha123");
        request.setSlug("petrobras");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/auth/login",
                request,
                AuthResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
        assertEquals("tenant_petrobras", response.getBody().getTenantId());
    }

    @Test
    void testLoginNegadoParaTenantIncorreto() {
        AuthRequest request = new AuthRequest();
        request.setEmail("admin@petrobras.com");
        request.setSenha("senha123");
        // O usuario é da petrobras, mas tenta acessar a URL da Vale
        request.setSlug("vale");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login",
                request,
                String.class
        );

        // Deve retornar 401 Unauthorized ou 403 Forbidden dependendo da configuração do error handler, 
        // mas definitivamente não 200 OK.
        assertNotEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getStatusCode() == HttpStatus.UNAUTHORIZED || response.getStatusCode() == HttpStatus.FORBIDDEN);
    }
}
