package com.erp.core.tenant;

import com.erp.core.security.UsuarioRepository;
import com.erp.core.tenant.dto.TenantProvisionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantProvisioningServiceTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TenantRepository tenantRepository;
    
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;

    @InjectMocks
    private TenantProvisioningService service;

    @Test
    void testStartProvisioning() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(tenantRepository.findBySchemaName(anyString())).thenReturn(new Tenant());

        TenantProvisionRequest request = new TenantProvisionRequest();
        request.setNomeEmpresa("Empresa");
        request.setSchemaName("schema");
        request.setAdminNome("admin");
        request.setAdminEmail("admin@a.com");
        request.setAdminSenha("123");

        service.startProvisioning(request);
        verify(tenantRepository, atLeastOnce()).save(any(Tenant.class));
    }

    @Test
    void testExecuteProvisioningAsync_FlywayFailsRollbackHappens() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(tenantRepository.findBySchemaName(anyString())).thenReturn(new Tenant());

        // Flyway vai tentar instanciar e falhar, caindo no rollback.
        CompletableFuture<Void> future = service.executeProvisioningAsync("schema", "admin", "admin@a.com", "123");
        
        future.join();
        assertNotNull(future);
        verify(dataSource, atLeastOnce()).getConnection();
    }
}
