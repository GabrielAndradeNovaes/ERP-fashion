package com.erp.production.repository;

import com.erp.production.domain.ApontamentoManual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ApontamentoManualRepository extends JpaRepository<ApontamentoManual, UUID> {

    @Query("SELECT a.funcionario.id, SUM(a.minutos) " +
           "FROM ApontamentoManual a " +
           "WHERE a.pago = false " +
           "AND a.dataHora BETWEEN :startDate AND :endDate " +
           "GROUP BY a.funcionario.id")
    List<Object[]> getSomaMinutosNaoPagos(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT a FROM ApontamentoManual a WHERE a.funcionario.id = :funcionarioId AND a.pago = false AND a.dataHora BETWEEN :startDate AND :endDate")
    List<ApontamentoManual> findNaoPagosByFuncionarioAndPeriod(
        @Param("funcionarioId") UUID funcionarioId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
}
