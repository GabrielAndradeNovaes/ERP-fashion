package com.erp.core.domain;

import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "funcionarios")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class Funcionario {

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 50, unique = true)
    private String matricula;

    @Column(name = "carga_horaria_diaria_padrao", nullable = false, precision = 5, scale = 2)
    private BigDecimal cargaHorariaDiariaPadrao;

    @Column(name = "carga_horaria_mensal_padrao", nullable = false, precision = 5, scale = 2)
    private BigDecimal cargaHorariaMensalPadrao;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "meta_minima", nullable = false, precision = 5, scale = 2)
    private BigDecimal metaMinima = new BigDecimal("75.00");

    @Column(name = "premio_100", nullable = false, precision = 10, scale = 2)
    private BigDecimal premio100 = new BigDecimal("1000.00");

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private GrupoFuncionario grupo;

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<FuncionarioJornada> jornadas = new java.util.ArrayList<>();

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public BigDecimal getCargaHorariaDiariaPadrao() { return cargaHorariaDiariaPadrao; }
    public void setCargaHorariaDiariaPadrao(BigDecimal cargaHorariaDiariaPadrao) { this.cargaHorariaDiariaPadrao = cargaHorariaDiariaPadrao; }
    public BigDecimal getCargaHorariaMensalPadrao() { return cargaHorariaMensalPadrao; }
    public void setCargaHorariaMensalPadrao(BigDecimal cargaHorariaMensalPadrao) { this.cargaHorariaMensalPadrao = cargaHorariaMensalPadrao; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public BigDecimal getMetaMinima() { return metaMinima; }
    public void setMetaMinima(BigDecimal metaMinima) { this.metaMinima = metaMinima; }
    public BigDecimal getPremio100() { return premio100; }
    public void setPremio100(BigDecimal premio100) { this.premio100 = premio100; }
    public GrupoFuncionario getGrupo() { return grupo; }
    public void setGrupo(GrupoFuncionario grupo) { this.grupo = grupo; }
    public java.util.List<FuncionarioJornada> getJornadas() { return jornadas; }
    public void setJornadas(java.util.List<FuncionarioJornada> jornadas) { this.jornadas = jornadas; }
}
