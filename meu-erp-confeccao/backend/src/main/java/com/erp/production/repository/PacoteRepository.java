package com.erp.production.repository;

import com.erp.production.domain.Pacote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PacoteRepository extends JpaRepository<Pacote, UUID> {
    List<Pacote> findByOrdemProducaoId(UUID ordemProducaoId);
}
