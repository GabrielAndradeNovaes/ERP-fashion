package com.erp.procurement.repository;

import com.erp.procurement.domain.OrdemCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrdemCompraRepository extends JpaRepository<OrdemCompra, UUID> {
    Page<OrdemCompra> findByNumeroPedidoContainingIgnoreCase(String numeroPedido, Pageable pageable);
}
