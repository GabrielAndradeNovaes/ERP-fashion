package com.erp.production.repository;

import com.erp.production.domain.FichaTecnica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FichaTecnicaRepository extends JpaRepository<FichaTecnica, UUID> {
}
