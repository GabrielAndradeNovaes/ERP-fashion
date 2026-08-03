package com.erp.production.repository;

import com.erp.production.domain.FaixaComprimentoCostura;
import com.erp.production.domain.GrauDificuldade;
import com.erp.production.domain.TabelaTempoPadrao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TabelaTempoPadraoRepository extends JpaRepository<TabelaTempoPadrao, UUID> {
    
    Optional<TabelaTempoPadrao> findByIndiceAndGrauDificuldadeAndFaixaComprimento(
            Integer indice, 
            GrauDificuldade grauDificuldade, 
            FaixaComprimentoCostura faixaComprimento
    );
}
