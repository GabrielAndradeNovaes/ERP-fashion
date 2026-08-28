package com.erp.core.tenant;

import com.erp.core.security.Usuario;
import com.erp.core.security.UsuarioRepository;
import com.erp.core.tenant.dto.TenantProvisionRequest;
import org.flywaydb.core.Flyway;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TenantProvisioningService {

    private static final Logger logger = LoggerFactory.getLogger(TenantProvisioningService.class);

    private final DataSource dataSource; // Master datasource
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantRepository tenantRepository;

    public TenantProvisioningService(DataSource dataSource, 
                                     UsuarioRepository usuarioRepository, 
                                     PasswordEncoder passwordEncoder,
                                     TenantRepository tenantRepository) {
        this.dataSource = dataSource;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tenantRepository = tenantRepository;
    }

    public void startProvisioning(TenantProvisionRequest request) {
        // 1. Registro síncrono no DB como PENDENTE
        insertTenantRecord(request, "PENDENTE");
        
        // 2. Dispara a orquestração pesada em background (Fire and Forget)
        executeProvisioningAsync(request.getSchemaName(), request.getAdminNome(), request.getAdminEmail(), request.getAdminSenha());
    }

    @Async
    public CompletableFuture<Void> executeProvisioningAsync(String schemaName, String adminNome, String adminEmail, String adminSenha) {
        try {
            updateTenantStatus(schemaName, "CRIANDO_INFRA");
            
            // 2. Criar o Schema no PostgreSQL
            createSchema(schemaName);

            // 3. Executar o Flyway Migration programaticamente para o novo schema
            runFlywayMigration(schemaName);

            // 4. Inserir o Usuário Admin (Seed)
            createAdminUser(schemaName, adminNome, adminEmail, adminSenha);

            updateTenantStatus(schemaName, "ATIVO");
            logger.info("Provisionamento do tenant {} concluído com sucesso.", schemaName);
            
        } catch (Exception e) {
            logger.error("Falha no provisionamento do tenant {}. Iniciando compensação (rollback)...", schemaName, e);
            rollbackSchema(schemaName);
            updateTenantStatus(schemaName, "FALHA");
        }
        
        return CompletableFuture.completedFuture(null);
    }

    private void updateTenantStatus(String schemaName, String status) {
        Tenant tenant = tenantRepository.findBySchemaName(schemaName);
        if (tenant != null) {
            tenant.setStatus(status);
            tenantRepository.save(tenant);
        } else {
            logger.error("Tenant {} não encontrado para atualizar o status para {}", schemaName, status);
        }
    }

    private void rollbackSchema(String schemaName) {
        String sql = "DROP SCHEMA IF EXISTS " + schemaName + " CASCADE";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
            logger.info("Rollback do schema {} efetuado com sucesso.", schemaName);
        } catch (SQLException e) {
            logger.error("Erro critico ao executar rollback do schema {}", schemaName, e);
        }
    }

    private void insertTenantRecord(TenantProvisionRequest request, String status) {
        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenant.setNomeEmpresa(request.getNomeEmpresa());
        tenant.setSchemaName(request.getSchemaName());
        tenant.setStatus(status);
        tenant.setAtivo(true);
        tenant.setCriadoEm(LocalDateTime.now());
        
        tenant.setCnpj(request.getCnpj());
        tenant.setRazaoSocial(request.getRazaoSocial());
        tenant.setNomeFantasia(request.getNomeFantasia());
        tenant.setPorte(request.getPorte());
        tenant.setNaturezaJuridica(request.getNaturezaJuridica());
        tenant.setStatusRfb(request.getStatusRfb());
        tenant.setDataAbertura(request.getDataAbertura());
        tenant.setEmailPrincipal(request.getEmailPrincipal());
        tenant.setTelefone(request.getTelefone());
        tenant.setCep(request.getCep());
        tenant.setLogradouro(request.getLogradouro());
        tenant.setNumero(request.getNumero());
        tenant.setComplemento(request.getComplemento());
        tenant.setBairro(request.getBairro());
        tenant.setCidade(request.getCidade());
        tenant.setEstado(request.getEstado());
        tenant.setCnaePrincipalCodigo(request.getCnaePrincipalCodigo());
        tenant.setCnaePrincipalDescricao(request.getCnaePrincipalDescricao());
        tenant.setSimplesNacional(request.getSimplesNacional());
        String rawData = request.getReceitaFederalRawData();
        tenant.setReceitaFederalRawData((rawData != null && rawData.trim().isEmpty()) ? null : rawData);

        tenantRepository.save(tenant);
    }

    private void createSchema(String schemaName) {
        String sql = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar schema: " + schemaName, e);
        }
    }

    private void runFlywayMigration(String schemaName) {
        Flyway flywayTenant = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .locations("classpath:db/migration/tenant")
                .baselineOnMigrate(true)
                .load();
        flywayTenant.migrate();
    }

    private void createAdminUser(String schemaName, String nome, String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setTenantId(schemaName);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setRole("ADMIN");
        usuario.setAtivo(true);
        usuario.setCriadoEm(LocalDateTime.now());
        
        usuarioRepository.save(usuario);
    }
}
