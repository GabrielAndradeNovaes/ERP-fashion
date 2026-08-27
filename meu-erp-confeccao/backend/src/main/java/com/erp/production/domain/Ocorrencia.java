package com.erp.production.domain;

import com.erp.core.domain.Empresa;
import org.hibernate.annotations.Filter;
import com.erp.core.domain.Funcionario;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ocorrencias")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class Ocorrencia {

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(name = "tempo_descontado_centesimal", nullable = false, precision = 5, scale = 2)
    private BigDecimal tempoDescontadoCentesimal;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @PrePersist
    protected void onCreate() {
        if (this.dataHora == null) {
            this.dataHora = LocalDateTime.now();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public BigDecimal getTempoDescontadoCentesimal() { return tempoDescontadoCentesimal; }
    public void setTempoDescontadoCentesimal(BigDecimal tempoDescontadoCentesimal) { this.tempoDescontadoCentesimal = tempoDescontadoCentesimal; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
