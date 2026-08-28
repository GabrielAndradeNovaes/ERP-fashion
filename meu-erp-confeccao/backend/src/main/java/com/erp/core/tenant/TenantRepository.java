package com.erp.core.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Tenant findBySchemaName(String schemaName);
    Optional<Tenant> findBySlug(String slug);
    List<Tenant> findAllByOrderByCriadoEmDesc();
}
