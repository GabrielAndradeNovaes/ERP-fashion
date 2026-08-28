package com.erp.catalog.repository;

import com.erp.catalog.domain.Cor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CorRepository extends JpaRepository<Cor, UUID> {
}
