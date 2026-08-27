package com.erp.core.security;

import com.erp.core.domain.Empresa;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "usuario_empresas")
@IdClass(UsuarioEmpresa.UsuarioEmpresaId.class)
public class UsuarioEmpresa {

    @Id
    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Id
    @Column(name = "empresa_id")
    private UUID empresaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    private Empresa empresa;

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(UUID empresaId) {
        this.empresaId = empresaId;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public static class UsuarioEmpresaId implements Serializable {
        private UUID usuarioId;
        private UUID empresaId;

        // default constructor, equals and hashcode
        public UsuarioEmpresaId() {}
        
        public UsuarioEmpresaId(UUID usuarioId, UUID empresaId) {
            this.usuarioId = usuarioId;
            this.empresaId = empresaId;
        }

        public UUID getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(UUID usuarioId) {
            this.usuarioId = usuarioId;
        }

        public UUID getEmpresaId() {
            return empresaId;
        }

        public void setEmpresaId(UUID empresaId) {
            this.empresaId = empresaId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UsuarioEmpresaId that = (UsuarioEmpresaId) o;
            return usuarioId.equals(that.usuarioId) && empresaId.equals(that.empresaId);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(usuarioId, empresaId);
        }
    }
}
