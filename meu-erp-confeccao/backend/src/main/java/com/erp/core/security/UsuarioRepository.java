package com.erp.core.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    @Query(value = "SELECT * FROM master.usuarios WHERE email = :email", nativeQuery = true)
    Optional<Usuario> findByEmail(@Param("email") String email);
}
