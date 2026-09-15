package com.erp.finance.repository;

import com.erp.finance.domain.TituloPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TituloPagarRepository extends JpaRepository<TituloPagar, UUID> {
    List<TituloPagar> findByStatus(TituloPagar.Status status);
    List<TituloPagar> findByDataVencimentoBetween(LocalDate start, LocalDate end);
}
