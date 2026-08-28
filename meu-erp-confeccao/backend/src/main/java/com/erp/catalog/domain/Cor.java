package com.erp.catalog.domain;

import com.erp.core.domain.Empresa;
import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cores")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class Cor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "codigo_hex", length = 20)
    private String codigoHex;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCodigoHex() { return codigoHex; }
    public void setCodigoHex(String codigoHex) { this.codigoHex = codigoHex; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
}
