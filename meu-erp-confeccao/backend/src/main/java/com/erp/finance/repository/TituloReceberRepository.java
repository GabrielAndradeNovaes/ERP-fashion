package com.erp.finance.repository;

import com.erp.finance.domain.TituloReceber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TituloReceberRepository extends JpaRepository<TituloReceber, UUID> {
    List<TituloReceber> findByStatus(TituloReceber.Status status);
    List<TituloReceber> findByDataVencimentoBetween(LocalDate start, LocalDate end);
}
