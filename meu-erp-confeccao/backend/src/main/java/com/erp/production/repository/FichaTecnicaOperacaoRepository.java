package com.erp.production.repository;

import com.erp.production.domain.FichaTecnicaOperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FichaTecnicaOperacaoRepository extends JpaRepository<FichaTecnicaOperacao, UUID> {
}
