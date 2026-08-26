package com.erp.core.tenant.controller;

import com.erp.core.tenant.TenantProvisioningService;
import com.erp.core.tenant.dto.TenantProvisionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/tenants")
public class AdminTenantController {

    private final TenantProvisioningService tenantProvisioningService;
    private final JdbcTemplate jdbcTemplate;

    public AdminTenantController(TenantProvisioningService tenantProvisioningService, JdbcTemplate jdbcTemplate) {
        this.tenantProvisioningService = tenantProvisioningService;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Apenas usuários com role SUPERADMIN ou similar (ajuste conforme a necessidade do negócio)
    // Para simplificar, vou restringir para "SUPERADMIN" (ou quem quer que tenha a role mestre).
    // Como a role atual do admin de tenant é "ADMIN", um super usuário teria "SUPERADMIN".
    @PostMapping("/provision")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> provisionTenant(@RequestBody TenantProvisionRequest request) {
        
        tenantProvisioningService.startProvisioning(
                request.getNomeEmpresa(),
                request.getSchemaName(),
                request.getAdminNome(),
                request.getAdminEmail(),
                request.getAdminSenha()
        );
        
        return ResponseEntity.accepted().body("Processo de provisionamento iniciado para " + request.getSchemaName());
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<Map<String, Object>>> listTenants() {
        String sql = "SELECT * FROM master.clientes_tenant ORDER BY criado_em DESC";
        return ResponseEntity.ok(jdbcTemplate.queryForList(sql));
    }

    @PutMapping("/{schemaName}/status")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> updateStatus(@PathVariable String schemaName, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status"); // ATIVO, INADIMPLENTE, CANCELADO
        String sql = "UPDATE master.clientes_tenant SET status = ? WHERE schema_name = ?";
        jdbcTemplate.update(sql, newStatus, schemaName);
        return ResponseEntity.ok("Status atualizado para " + newStatus);
    }
}
