package com.erp.core.tenant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AcessoLogRepository extends JpaRepository<AcessoLog, UUID> {
    long countByDataAcessoAfter(LocalDateTime dateTime);
}
