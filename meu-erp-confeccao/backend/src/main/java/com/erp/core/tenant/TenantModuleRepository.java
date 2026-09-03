package com.erp.core.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantModuleRepository extends JpaRepository<TenantModule, UUID> {
    List<TenantModule> findByTenantId(String tenantId);
    Optional<TenantModule> findByTenantIdAndModuleName(String tenantId, String moduleName);
}
