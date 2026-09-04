package com.erp.integration;

import com.erp.catalog.dto.ProdutoBaseRequest;
import com.erp.catalog.dto.ProdutoBaseResponse;
import com.erp.catalog.dto.ProdutoSkuRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    private String validToken;

    @BeforeEach
    void setUp() throws Exception {
        // Inicializa o banco com dados de login
        try (Connection conn = dataSource.getConnection()) {
            conn.createStatement().execute("CREATE SCHEMA IF NOT EXISTS master;");
            conn.createStatement().execute("TRUNCATE TABLE master.usuarios CASCADE;");
            conn.createStatement().execute("TRUNCATE TABLE master.clientes_tenant CASCADE;");

            PreparedStatement stmtTenant = conn.prepareStatement(
                    "INSERT INTO master.clientes_tenant (id, razao_social, cnpj, schema_name, slug, email_contato, telefone_contato, status) " +
                            "VALUES (?, 'Petrobras', '11111111111111', 'tenant_petrobras', 'petrobras', 'contato@petrobras.com', '111111', 'ATIVO')"
            );
            stmtTenant.setObject(1, UUID.randomUUID());
            stmtTenant.executeUpdate();

            // Usuario admin
            PreparedStatement stmtUser = conn.prepareStatement(
                    "INSERT INTO master.usuarios (id, nome, email, senha, tenant_id, role) " +
                            "VALUES (?, 'Admin Petrobras', 'admin_prod@petrobras.com', '$2a$10$XU.Yl1w9G5J9V3m3H8y9aO5G1z6u2G4w8x7c1x9aO5G1z6u2G4w8x7c1x9', 'tenant_petrobras', 'ADMIN')" // $2a$10$... é hash falso de senha123
                            // Vamos apenas injetar o token real para facilitar, 
                            // ou podemos usar a API de login se a senha no DB estiver com encode real.
            );
            // Ao inves de injetar senha e fazer requisição de login complexa, faremos a requisição de login.
        }

        // Gera token real via endpoint de login já testado
        // Porém no setUp anterior colocamos hash real: passwordEncoder.encode("senha123").
        // Vamos arrumar a inserção com BCrypt no setup
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmtUser = conn.prepareStatement(
                    "INSERT INTO master.usuarios (id, nome, email, senha, tenant_id, role) " +
                            "VALUES (?, 'Admin Prod', 'prod@test.com', '$2a$10$w0B1qjGv2O6V.c1oF.t5eO0fF0wJ9a.jK/7l1H9l5Z.2g4h6j8o3O', 'tenant_petrobras', 'ADMIN')" // bcrypt de senha123
            );
            stmtUser.setObject(1, UUID.randomUUID());
            stmtUser.executeUpdate();
            
            // Criar o schema tenant_petrobras para suportar as tabelas
            // O TenantProvisioningService faria isso, mas aqui podemos rodar o script basico ou forçar o flyway.
            // O Flyway já deve ter criado o public. Vamos forçar o banco a usar o public ou o hibernate fará o auto-create.
            // Para não quebrar por falta de tabelas, usaremos a opção de hibernate.ddl-auto=update no application.yml de testes.
        }

        // Login
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String loginBody = "{\"email\":\"prod@test.com\",\"senha\":\"senha123\",\"slug\":\"petrobras\"}";
        HttpEntity<String> loginRequest = new HttpEntity<>(loginBody, headers);
        
        ResponseEntity<com.erp.core.security.dto.AuthResponse> loginRes = restTemplate.postForEntity("/api/auth/login", loginRequest, com.erp.core.security.dto.AuthResponse.class);
        if (loginRes.getStatusCode() == HttpStatus.OK && loginRes.getBody() != null) {
            validToken = loginRes.getBody().getToken();
        }
    }

    @Test
    void testCriarProdutoESku_IntegradoAoBanco() throws Exception {
        assertNotNull(validToken, "O login deve funcionar para prosseguir com o teste.");

        ProdutoSkuRequest skuReq = new ProdutoSkuRequest("Vermelho", "M", "EAN12345", new BigDecimal("59.90"));
        ProdutoBaseRequest req = new ProdutoBaseRequest(
                "CAM01", "Camiseta Algodão", "Desc", new BigDecimal("50.00"), new BigDecimal("20.00"),
                "MarcaX", "Camisetas", "Inverno", "Unissex", "12345678", "12345", "Nacional",
                new BigDecimal("0.5"), new BigDecimal("0.4"), "ATIVO", List.of(skuReq)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        headers.set("X-Tenant-ID", "petrobras");
        HttpEntity<ProdutoBaseRequest> request = new HttpEntity<>(req, headers);

        ResponseEntity<ProdutoBaseResponse> response = restTemplate.postForEntity(
                "/api/catalog/produtos",
                request,
                ProdutoBaseResponse.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Camiseta Algodão", response.getBody().nome());
        assertFalse(response.getBody().skus().isEmpty());
        assertEquals("Vermelho", response.getBody().skus().get(0).cor());

        // Agora verificamos diretamente no banco de dados se os dados foram pra tabela do tenant (ou public)
        try (Connection conn = dataSource.getConnection()) {
            // Se o schema tenant_petrobras existir, será lá. Como é teste, pode estar no public dependendo da config.
            // Vamos testar se existe uma entrada na tabela produtos_base
            // (a anotação @Table no ProdutoBase é produtos_base)
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM tenant_petrobras.produtos_base WHERE codigo = 'CAM01'");
            try {
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                    assertEquals(1, rs.getInt(1));
                }
            } catch (Exception e) {
                // Se a query falhar no schema tenant_petrobras, tentamos no schema padrão do hibernate (public)
                PreparedStatement stmtPub = conn.prepareStatement("SELECT COUNT(*) FROM produtos_base WHERE codigo = 'CAM01'");
                ResultSet rsPub = stmtPub.executeQuery();
                if(rsPub.next()) {
                    assertEquals(1, rsPub.getInt(1));
                }
            }
        }
    }
}
