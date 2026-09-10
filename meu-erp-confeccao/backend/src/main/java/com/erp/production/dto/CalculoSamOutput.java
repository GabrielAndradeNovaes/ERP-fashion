package com.erp.production.dto;

import java.math.BigDecimal;

public class CalculoSamOutput {
    private Integer paradasUtilizadas;
    private BigDecimal tempoMaquinaSegundos;
    private BigDecimal tempoManualSegundos;
    private BigDecimal tempoPadraoSegundos;
    private BigDecimal samMinutos;

    public CalculoSamOutput() {}

    public CalculoSamOutput(Integer paradasUtilizadas, BigDecimal tempoMaquinaSegundos, BigDecimal tempoManualSegundos, BigDecimal tempoPadraoSegundos, BigDecimal samMinutos) {
        this.paradasUtilizadas = paradasUtilizadas;
        this.tempoMaquinaSegundos = tempoMaquinaSegundos;
        this.tempoManualSegundos = tempoManualSegundos;
        this.tempoPadraoSegundos = tempoPadraoSegundos;
        this.samMinutos = samMinutos;
    }

    public Integer getParadasUtilizadas() { return paradasUtilizadas; }
    public void setParadasUtilizadas(Integer paradasUtilizadas) { this.paradasUtilizadas = paradasUtilizadas; }

    public BigDecimal getTempoMaquinaSegundos() { return tempoMaquinaSegundos; }
    public void setTempoMaquinaSegundos(BigDecimal tempoMaquinaSegundos) { this.tempoMaquinaSegundos = tempoMaquinaSegundos; }

    public BigDecimal getTempoManualSegundos() { return tempoManualSegundos; }
    public void setTempoManualSegundos(BigDecimal tempoManualSegundos) { this.tempoManualSegundos = tempoManualSegundos; }

    public BigDecimal getTempoPadraoSegundos() { return tempoPadraoSegundos; }
    public void setTempoPadraoSegundos(BigDecimal tempoPadraoSegundos) { this.tempoPadraoSegundos = tempoPadraoSegundos; }

    public BigDecimal getSamMinutos() { return samMinutos; }
    public void setSamMinutos(BigDecimal samMinutos) { this.samMinutos = samMinutos; }
}
