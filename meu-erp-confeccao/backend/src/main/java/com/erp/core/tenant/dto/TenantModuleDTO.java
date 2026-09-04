package com.erp.core.tenant.dto;

import java.util.UUID;

public class TenantModuleDTO {
    private String moduleName;
    private boolean active;

    public TenantModuleDTO() {}

    public TenantModuleDTO(String moduleName, boolean active) {
        this.moduleName = moduleName;
        this.active = active;
    }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
