package com.erp.core.domain;

import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "grupos_funcionarios")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class GrupoFuncionario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @Column(nullable = false, length = 100)
    private String nome;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
