package com.erp.core.domain;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "funcionario_jornadas")
public class FuncionarioJornada {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana; // 1=Monday, 7=Sunday

    @Column(nullable = false)
    private LocalTime entrada;

    @Column(nullable = false)
    private LocalTime saida;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
    public Integer getDiaSemana() { return diaSemana; }
    public void setDiaSemana(Integer diaSemana) { this.diaSemana = diaSemana; }
    public LocalTime getEntrada() { return entrada; }
    public void setEntrada(LocalTime entrada) { this.entrada = entrada; }
    public LocalTime getSaida() { return saida; }
    public void setSaida(LocalTime saida) { this.saida = saida; }
}
