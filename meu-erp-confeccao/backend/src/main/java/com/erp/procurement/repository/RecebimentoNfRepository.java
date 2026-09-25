package com.erp.procurement.repository;

import com.erp.procurement.domain.RecebimentoNf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecebimentoNfRepository extends JpaRepository<RecebimentoNf, UUID> {
    Page<RecebimentoNf> findByNumeroNfeContainingIgnoreCase(String numeroNfe, Pageable pageable);
}
