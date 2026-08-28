package com.erp.production.repository;

import com.erp.production.domain.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, UUID> {
    Optional<Cupom> findByCodigoBarras(String codigoBarras);
    
    @Query("SELECT c FROM Cupom c JOIN c.pacote p JOIN p.ordemProducao o WHERE o.id = :ordemProducaoId")
    List<Cupom> findByOrdemProducaoId(@Param("ordemProducaoId") UUID ordemProducaoId);
    
    List<Cupom> findByPacoteId(UUID pacoteId);
}
