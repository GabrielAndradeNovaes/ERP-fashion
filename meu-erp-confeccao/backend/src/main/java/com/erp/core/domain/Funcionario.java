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
}
