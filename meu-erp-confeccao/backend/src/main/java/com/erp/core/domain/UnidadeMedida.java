package com.erp.core.domain;

import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "unidades_medida")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
@SQLDelete(sql = "UPDATE unidades_medida SET deleted = true WHERE id=?")
public class UnidadeMedida {

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;



    @Column(nullable = false)
    private String sigla;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private boolean deleted = false;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
