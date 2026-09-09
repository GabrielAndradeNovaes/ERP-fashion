package com.erp.production.domain;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Funcionario;
import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "apontamentos_manuais")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class ApontamentoManual {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Empresa empresa;

    @ManyToOne(optional = false)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private Integer minutos;

    @Column(length = 500)
    private String observacao;

    @Column(nullable = false)
    private boolean pago = false;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @PrePersist
    protected void onCreate() {
        if (this.dataHora == null) {
            this.dataHora = LocalDateTime.now();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Integer getMinutos() { return minutos; }
    public void setMinutos(Integer minutos) { this.minutos = minutos; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public boolean isPago() { return pago; }
    public void setPago(boolean pago) { this.pago = pago; }

    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }
}
