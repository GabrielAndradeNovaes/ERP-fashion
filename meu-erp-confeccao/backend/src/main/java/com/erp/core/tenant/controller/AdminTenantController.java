package com.erp.core.tenant.controller;

import com.erp.core.tenant.Tenant;
import com.erp.core.tenant.TenantRepository;
import com.erp.core.tenant.TenantProvisioningService;
import com.erp.core.tenant.dto.TenantProvisionRequest;
import com.erp.core.tenant.dto.TenantResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/tenants")
public class AdminTenantController {

    private final TenantProvisioningService tenantProvisioningService;
    private final TenantRepository tenantRepository;

    public AdminTenantController(TenantProvisioningService tenantProvisioningService, TenantRepository tenantRepository) {
        this.tenantProvisioningService = tenantProvisioningService;
        this.tenantRepository = tenantRepository;
    }

    // Apenas usuários com role SUPERADMIN ou similar (ajuste conforme a necessidade do negócio)
    // Para simplificar, vou restringir para "SUPERADMIN" (ou quem quer que tenha a role mestre).
    // Como a role atual do admin de tenant é "ADMIN", um super usuário teria "SUPERADMIN".
    @PostMapping("/provision")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> provisionTenant(@RequestBody TenantProvisionRequest request) {
        
        String schemaName = request.getSchemaName();
        if (schemaName == null || schemaName.trim().isEmpty()) {
            // Gera um schemaId baseado em UUID (ex: tenant_4b2a9f)
            schemaName = "tenant_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            request.setSchemaName(schemaName);
        }

        tenantProvisioningService.startProvisioning(request);
        
        return ResponseEntity.accepted().body("Processo de provisionamento iniciado para " + schemaName);
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<TenantResponse>> listTenants() {
        List<Tenant> tenants = tenantRepository.findAllByOrderByCriadoEmDesc();
        List<TenantResponse> responses = tenants.stream()
                .map(TenantResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{schemaName}/status")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> updateStatus(@PathVariable String schemaName, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status"); // ATIVO, INADIMPLENTE, CANCELADO
        Tenant tenant = tenantRepository.findBySchemaName(schemaName);
        if (tenant != null) {
            tenant.setStatus(newStatus);
            tenantRepository.save(tenant);
            return ResponseEntity.ok("Status atualizado para " + newStatus);
        }
        return ResponseEntity.notFound().build();
    }
    @PutMapping("/{schemaName}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<TenantResponse> updateTenant(@PathVariable String schemaName, @RequestBody com.erp.core.tenant.dto.TenantUpdateRequest request) {
        Tenant tenant = tenantRepository.findBySchemaName(schemaName);
        if (tenant == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.getNomeEmpresa() != null) tenant.setNomeEmpresa(request.getNomeEmpresa());
        if (request.getCnpj() != null) tenant.setCnpj(request.getCnpj());
        if (request.getRazaoSocial() != null) tenant.setRazaoSocial(request.getRazaoSocial());
        if (request.getNomeFantasia() != null) tenant.setNomeFantasia(request.getNomeFantasia());
        if (request.getPorte() != null) tenant.setPorte(request.getPorte());
        if (request.getNaturezaJuridica() != null) tenant.setNaturezaJuridica(request.getNaturezaJuridica());
        if (request.getStatusRfb() != null) tenant.setStatusRfb(request.getStatusRfb());
        if (request.getDataAbertura() != null) tenant.setDataAbertura(request.getDataAbertura());
        if (request.getEmailPrincipal() != null) tenant.setEmailPrincipal(request.getEmailPrincipal());
        if (request.getTelefone() != null) tenant.setTelefone(request.getTelefone());
        if (request.getCep() != null) tenant.setCep(request.getCep());
        if (request.getLogradouro() != null) tenant.setLogradouro(request.getLogradouro());
        if (request.getNumero() != null) tenant.setNumero(request.getNumero());
        if (request.getComplemento() != null) tenant.setComplemento(request.getComplemento());
        if (request.getBairro() != null) tenant.setBairro(request.getBairro());
        if (request.getCidade() != null) tenant.setCidade(request.getCidade());
        if (request.getEstado() != null) tenant.setEstado(request.getEstado());
        if (request.getCnaePrincipalCodigo() != null) tenant.setCnaePrincipalCodigo(request.getCnaePrincipalCodigo());
        if (request.getCnaePrincipalDescricao() != null) tenant.setCnaePrincipalDescricao(request.getCnaePrincipalDescricao());
        if (request.getSimplesNacional() != null) tenant.setSimplesNacional(request.getSimplesNacional());
        if (request.getReceitaFederalRawData() != null) {
            String rawData = request.getReceitaFederalRawData();
            tenant.setReceitaFederalRawData(rawData.trim().isEmpty() ? null : rawData);
        }

        tenantRepository.save(tenant);
        return ResponseEntity.ok(new TenantResponse(tenant));
    }
}
