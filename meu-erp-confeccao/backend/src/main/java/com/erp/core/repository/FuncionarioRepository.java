package com.erp.core.repository;

import com.erp.core.domain.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, UUID> {
    Optional<Funcionario> findByMatricula(String matricula);
    java.util.List<Funcionario> findByAtivoTrue();
    java.util.List<Funcionario> findByGrupoId(UUID grupoId);
    
    @org.springframework.data.jpa.repository.Query("SELECT f FROM Funcionario f WHERE LOWER(f.grupo.nome) = 'produção'")
    java.util.List<Funcionario> findByGrupoProducao();
}
