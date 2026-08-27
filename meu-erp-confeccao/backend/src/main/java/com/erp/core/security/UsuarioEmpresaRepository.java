package com.erp.core.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UsuarioEmpresaRepository extends JpaRepository<UsuarioEmpresa, UsuarioEmpresa.UsuarioEmpresaId> {
    List<UsuarioEmpresa> findByUsuarioId(UUID usuarioId);
}
