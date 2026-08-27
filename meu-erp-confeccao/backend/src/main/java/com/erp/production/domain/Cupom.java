package com.erp.production.domain;

import com.erp.core.domain.Empresa;
import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cupons")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class Cupom {

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }


    public enum Status {
        PENDENTE, PROCESSADO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pacote_id", nullable = false)
    private Pacote pacote;

    @ManyToOne(optional = false)
    @JoinColumn(name = "operacao_id", nullable = false)
    private FichaTecnicaOperacao operacao;

    @Column(name = "codigo_barras", nullable = false, unique = true, length = 100)
    private String codigoBarras;

    @Column(name = "tempo_total_centesimal", nullable = false, precision = 10, scale = 2)
    private BigDecimal tempoTotalCentesimal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDENTE;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Pacote getPacote() { return pacote; }
    public void setPacote(Pacote pacote) { this.pacote = pacote; }
    public FichaTecnicaOperacao getOperacao() { return operacao; }
    public void setOperacao(FichaTecnicaOperacao operacao) { this.operacao = operacao; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    public BigDecimal getTempoTotalCentesimal() { return tempoTotalCentesimal; }
    public void setTempoTotalCentesimal(BigDecimal tempoTotalCentesimal) { this.tempoTotalCentesimal = tempoTotalCentesimal; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
