package com.erp.core.repository;

import com.erp.core.domain.GrupoFuncionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface GrupoFuncionarioRepository extends JpaRepository<GrupoFuncionario, UUID> {
    List<GrupoFuncionario> findByNomeContainingIgnoreCase(String nome);
}
