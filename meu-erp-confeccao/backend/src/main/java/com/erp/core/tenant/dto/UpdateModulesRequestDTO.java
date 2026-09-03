package com.erp.core.tenant.dto;

import java.util.List;

public class UpdateModulesRequestDTO {
    private List<TenantModuleDTO> modules;

    public UpdateModulesRequestDTO() {}

    public UpdateModulesRequestDTO(List<TenantModuleDTO> modules) {
        this.modules = modules;
    }

    public List<TenantModuleDTO> getModules() { return modules; }
    public void setModules(List<TenantModuleDTO> modules) { this.modules = modules; }
}
