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

    @Query("SELECT new com.erp.production.dto.ProdutividadeResumo(a.funcionario.id, a.funcionario.nome, COUNT(a.id), SUM(a.cupom.tempoTotalCentesimal)) " +
           "FROM Apontamento a " +
           "WHERE a.pago = false " +
           "AND a.dataHora BETWEEN :startDate AND :endDate " +
           "GROUP BY a.funcionario.id, a.funcionario.nome")
    List<com.erp.production.dto.ProdutividadeResumo> getProdutividadeResumo(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT a FROM Apontamento a WHERE a.funcionario.id = :funcionarioId AND a.pago = false AND a.dataHora BETWEEN :startDate AND :endDate")
    List<Apontamento> findNaoPagosByFuncionarioAndPeriod(
        @Param("funcionarioId") UUID funcionarioId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
}
