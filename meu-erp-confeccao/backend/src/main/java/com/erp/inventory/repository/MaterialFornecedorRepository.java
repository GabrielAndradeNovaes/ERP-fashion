package com.erp.inventory.repository;

import com.erp.inventory.domain.MaterialFornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialFornecedorRepository extends JpaRepository<MaterialFornecedor, UUID> {
    
    Optional<MaterialFornecedor> findByFornecedorIdAndCodigoFornecedor(UUID fornecedorId, String codigoFornecedor);
    
    List<MaterialFornecedor> findByFornecedorId(UUID fornecedorId);
    
    List<MaterialFornecedor> findByMaterialId(UUID materialId);
}
