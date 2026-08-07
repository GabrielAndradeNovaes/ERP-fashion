package com.erp.production.repository;

import com.erp.production.domain.OrdemProducao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrdemProducaoRepository extends JpaRepository<OrdemProducao, UUID> {
    boolean existsByNumero(String numero);
    long countByStatus(com.erp.production.domain.OrdemProducaoStatus status);
}
