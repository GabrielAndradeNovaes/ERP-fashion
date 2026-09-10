package com.erp.core.tenant.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TenantDtoTest {

    @Test
    void testAdminDashboardMetricsDTO() {
        AdminDashboardMetricsDTO dto = new AdminDashboardMetricsDTO();
        dto.setTotalTenants(10);
        dto.setActiveTenants(5);
        dto.setInactiveTenants(2);
        dto.setPendingTenants(3);
        dto.setEstimatedMRR(1500.0);

        assertEquals(10, dto.getTotalTenants());
        assertEquals(5, dto.getActiveTenants());
        assertEquals(2, dto.getInactiveTenants());
        assertEquals(3, dto.getPendingTenants());
        assertEquals(1500.0, dto.getEstimatedMRR());

        AdminDashboardMetricsDTO dto2 = new AdminDashboardMetricsDTO(10, 5, 2, 3, 1500.0);
        assertEquals(10, dto2.getTotalTenants());
    }

    @Test
    void testTenantModuleDTO() {
        TenantModuleDTO dto = new TenantModuleDTO();
        dto.setModuleName("CRM");
        dto.setActive(true);

        assertEquals("CRM", dto.getModuleName());
        assertTrue(dto.isActive());

        TenantModuleDTO dto2 = new TenantModuleDTO("ERP", false);
        assertEquals("ERP", dto2.getModuleName());
        assertFalse(dto2.isActive());
    }

    @Test
    void testUpdateModulesRequestDTO() {
        UpdateModulesRequestDTO dto = new UpdateModulesRequestDTO();
        List<TenantModuleDTO> modules = List.of(new TenantModuleDTO("CRM", true), new TenantModuleDTO("ERP", false));
        dto.setModules(modules);
        
        assertNotNull(dto.getModules());
        assertEquals(2, dto.getModules().size());
        
        UpdateModulesRequestDTO dto2 = new UpdateModulesRequestDTO(modules);
        assertEquals(2, dto2.getModules().size());
    }

    @Test
    void testTenantProvisionRequest() {
        TenantProvisionRequest request = new TenantProvisionRequest();
        request.setNomeEmpresa("Empresa X");
        request.setSchemaName("schema_x");
        request.setAdminNome("Admin");
        request.setAdminEmail("admin@x.com");
        request.setAdminSenha("123456");
        request.setCnpj("12.345.678/0001-99");
        request.setRazaoSocial("Empresa X LTDA");
        request.setNomeFantasia("Empresa X");
        request.setPorte("ME");
        request.setNaturezaJuridica("LTDA");
        request.setStatusRfb("ATIVA");
        request.setDataAbertura(java.time.LocalDate.of(2020, 1, 1));
        request.setEmailPrincipal("contato@x.com");
        request.setTelefone("11999999999");
        request.setCep("01000-000");
        request.setLogradouro("Rua X");
        request.setNumero("100");
        request.setComplemento("Sala 1");
        request.setBairro("Centro");
        request.setCidade("São Paulo");
        request.setEstado("SP");
        request.setCnaePrincipalCodigo("1234-5/67");
        request.setCnaePrincipalDescricao("Desenvolvimento de software");
        request.setSimplesNacional(true);
        request.setReceitaFederalRawData("{}");

        assertEquals("Empresa X", request.getNomeEmpresa());
        assertEquals("schema_x", request.getSchemaName());
        assertEquals("Admin", request.getAdminNome());
        assertEquals("admin@x.com", request.getAdminEmail());
        assertEquals("123456", request.getAdminSenha());
        assertEquals("12.345.678/0001-99", request.getCnpj());
        assertEquals("Empresa X LTDA", request.getRazaoSocial());
        assertEquals("Empresa X", request.getNomeFantasia());
        assertEquals("ME", request.getPorte());
        assertEquals("LTDA", request.getNaturezaJuridica());
        assertEquals("ATIVA", request.getStatusRfb());
        assertNotNull(request.getDataAbertura());
        assertEquals("contato@x.com", request.getEmailPrincipal());
        assertEquals("11999999999", request.getTelefone());
        assertEquals("01000-000", request.getCep());
        assertEquals("Rua X", request.getLogradouro());
        assertEquals("100", request.getNumero());
        assertEquals("Sala 1", request.getComplemento());
        assertEquals("Centro", request.getBairro());
        assertEquals("São Paulo", request.getCidade());
        assertEquals("SP", request.getEstado());
        assertEquals("1234-5/67", request.getCnaePrincipalCodigo());
        assertEquals("Desenvolvimento de software", request.getCnaePrincipalDescricao());
        assertTrue(request.getSimplesNacional());
        assertEquals("{}", request.getReceitaFederalRawData());
    }

    @Test
    void testTenantResponse() {
        com.erp.core.tenant.Tenant tenant = new com.erp.core.tenant.Tenant();
        java.util.UUID id = java.util.UUID.randomUUID();
        tenant.setId(id);
        tenant.setSchemaName("schema_y");
        tenant.setNomeEmpresa("Empresa Y");
        tenant.setEmailPrincipal("admin@y.com");
        tenant.setStatus("ATIVO");
        tenant.setCnpj("00.000.000/0001-00");
        tenant.setDataAbertura(java.time.LocalDate.now());
        tenant.setTelefone("11999999999");
        tenant.setCidade("Rio de Janeiro");
        tenant.setEstado("RJ");
        tenant.setCriadoEm(java.time.LocalDateTime.now());
        
        TenantResponse response = new TenantResponse(tenant);

        assertEquals(id, response.getId());
        assertEquals("schema_y", response.getSchemaName());
        assertEquals("Empresa Y", response.getNomeEmpresa());
        assertEquals("admin@y.com", response.getEmailPrincipal());
        assertEquals("ATIVO", response.getStatus());
        assertEquals("00.000.000/0001-00", response.getCnpj());
        assertNotNull(response.getDataAbertura());
        assertEquals("11999999999", response.getTelefone());
        assertEquals("Rio de Janeiro", response.getCidade());
        assertEquals("RJ", response.getEstado());
        assertNotNull(response.getCriadoEm());
    }

    @Test
    void testTenantUpdateRequest() {
        TenantUpdateRequest request = new TenantUpdateRequest();
        request.setNomeEmpresa("Nova Empresa");
        request.setCnpj("11.111.111/0001-11");
        request.setRazaoSocial("Nova Razao");
        request.setNomeFantasia("Nova Fantasia");
        request.setPorte("EPP");
        request.setNaturezaJuridica("SA");
        request.setDataAbertura(java.time.LocalDate.now());
        request.setEmailPrincipal("novo@email.com");
        request.setTelefone("222222222");
        request.setCep("22222-222");
        request.setLogradouro("Rua Y");
        request.setNumero("200");
        request.setComplemento("Apto 2");
        request.setBairro("Sul");
        request.setCidade("Curitiba");
        request.setEstado("PR");
        request.setCnaePrincipalCodigo("0000-0/00");
        request.setCnaePrincipalDescricao("Outro dev");
        request.setSimplesNacional(false);

        assertEquals("Nova Empresa", request.getNomeEmpresa());
        assertEquals("11.111.111/0001-11", request.getCnpj());
        assertEquals("Nova Razao", request.getRazaoSocial());
        assertEquals("Nova Fantasia", request.getNomeFantasia());
        assertEquals("EPP", request.getPorte());
        assertEquals("SA", request.getNaturezaJuridica());
        assertNotNull(request.getDataAbertura());
        assertEquals("novo@email.com", request.getEmailPrincipal());
        assertEquals("222222222", request.getTelefone());
        assertEquals("22222-222", request.getCep());
        assertEquals("Rua Y", request.getLogradouro());
        assertEquals("200", request.getNumero());
        assertEquals("Apto 2", request.getComplemento());
        assertEquals("Sul", request.getBairro());
        assertEquals("Curitiba", request.getCidade());
        assertEquals("PR", request.getEstado());
        assertEquals("0000-0/00", request.getCnaePrincipalCodigo());
        assertEquals("Outro dev", request.getCnaePrincipalDescricao());
        assertFalse(request.getSimplesNacional());
    }
}
