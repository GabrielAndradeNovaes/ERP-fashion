package com.erp.catalog.repository;

import com.erp.catalog.domain.ProdutoBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProdutoBaseRepository extends JpaRepository<ProdutoBase, UUID> {
    Optional<ProdutoBase> findByCodigo(String codigo);
}
