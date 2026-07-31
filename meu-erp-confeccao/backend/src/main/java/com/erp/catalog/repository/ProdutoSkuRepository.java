package com.erp.catalog.repository;

import com.erp.catalog.domain.ProdutoSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProdutoSkuRepository extends JpaRepository<ProdutoSku, UUID> {
    List<ProdutoSku> findByProdutoBaseId(UUID produtoBaseId);
    Optional<ProdutoSku> findByCodigoBarras(String codigoBarras);
}
