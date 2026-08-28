package com.erp.catalog.repository;

import com.erp.catalog.domain.UnidadeMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UnidadeMedidaRepository extends JpaRepository<UnidadeMedida, UUID> {
}
