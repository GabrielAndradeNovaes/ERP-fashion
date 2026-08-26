package com.erp.production.repository;

import com.erp.production.domain.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.LocalDateTime;

import java.util.UUID;

@Repository
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, UUID> {
    
    @Query("SELECT o FROM Ocorrencia o WHERE o.funcionario.id = :funcionarioId AND o.dataHora BETWEEN :startDate AND :endDate")
    List<Ocorrencia> findByFuncionarioAndPeriod(@Param("funcionarioId") UUID funcionarioId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
