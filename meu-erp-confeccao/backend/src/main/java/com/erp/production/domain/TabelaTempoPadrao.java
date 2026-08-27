package com.erp.production.domain;

import com.erp.core.domain.Empresa;
import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tabela_tempo_padrao", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"indice", "grau_dificuldade", "faixa_comprimento"})
})
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class TabelaTempoPadrao {

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Integer indice;

    @Enumerated(EnumType.STRING)
    @Column(name = "grau_dificuldade", nullable = false, length = 30)
    private GrauDificuldade grauDificuldade;

    @Enumerated(EnumType.STRING)
    @Column(name = "faixa_comprimento", nullable = false, length = 30)
    private FaixaComprimentoCostura faixaComprimento;

    @Column(name = "tempo_centesimal", nullable = false, precision = 10, scale = 2)
    private BigDecimal tempoCentesimal;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Integer getIndice() { return indice; }
    public void setIndice(Integer indice) { this.indice = indice; }
    public GrauDificuldade getGrauDificuldade() { return grauDificuldade; }
    public void setGrauDificuldade(GrauDificuldade grauDificuldade) { this.grauDificuldade = grauDificuldade; }
    public FaixaComprimentoCostura getFaixaComprimento() { return faixaComprimento; }
    public void setFaixaComprimento(FaixaComprimentoCostura faixaComprimento) { this.faixaComprimento = faixaComprimento; }
    public BigDecimal getTempoCentesimal() { return tempoCentesimal; }
    public void setTempoCentesimal(BigDecimal tempoCentesimal) { this.tempoCentesimal = tempoCentesimal; }
}
