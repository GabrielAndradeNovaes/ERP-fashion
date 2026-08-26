package com.erp.production.repository;

import com.erp.production.domain.Apontamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.LocalDateTime;

import java.util.UUID;

@Repository
public interface ApontamentoRepository extends JpaRepository<Apontamento, UUID> {
    
    @Query("SELECT a FROM Apontamento a WHERE a.funcionario.id = :funcionarioId AND a.dataHora BETWEEN :startDate AND :endDate")
    List<Apontamento> findByFuncionarioAndPeriod(@Param("funcionarioId") UUID funcionarioId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
