package com.erp.catalog.repository;

import com.erp.catalog.domain.Tamanho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TamanhoRepository extends JpaRepository<Tamanho, UUID> {
}
