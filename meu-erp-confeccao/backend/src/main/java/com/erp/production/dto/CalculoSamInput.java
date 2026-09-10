package com.erp.production.dto;

import com.erp.production.domain.DificuldadeTecido;
import com.erp.production.domain.TipoTrajeto;
import java.math.BigDecimal;

public class CalculoSamInput {
    private Integer rpmMaquina;
    private BigDecimal pontosPorCm;
    private BigDecimal comprimentoCosturaCm;
    private Integer quantidadeFolhas;
    private DificuldadeTecido dificuldadeTecido;
    private TipoTrajeto tipoTrajeto;
    private Integer paradasForcadas;

    public CalculoSamInput() {}

    public CalculoSamInput(Integer rpmMaquina, BigDecimal pontosPorCm, BigDecimal comprimentoCosturaCm, Integer quantidadeFolhas, DificuldadeTecido dificuldadeTecido, TipoTrajeto tipoTrajeto, Integer paradasForcadas) {
        this.rpmMaquina = rpmMaquina;
        this.pontosPorCm = pontosPorCm;
        this.comprimentoCosturaCm = comprimentoCosturaCm;
        this.quantidadeFolhas = quantidadeFolhas;
        this.dificuldadeTecido = dificuldadeTecido;
        this.tipoTrajeto = tipoTrajeto;
        this.paradasForcadas = paradasForcadas;
    }

    public Integer getRpmMaquina() { return rpmMaquina; }
    public void setRpmMaquina(Integer rpmMaquina) { this.rpmMaquina = rpmMaquina; }

    public BigDecimal getPontosPorCm() { return pontosPorCm; }
    public void setPontosPorCm(BigDecimal pontosPorCm) { this.pontosPorCm = pontosPorCm; }

    public BigDecimal getComprimentoCosturaCm() { return comprimentoCosturaCm; }
    public void setComprimentoCosturaCm(BigDecimal comprimentoCosturaCm) { this.comprimentoCosturaCm = comprimentoCosturaCm; }

    public Integer getQuantidadeFolhas() { return quantidadeFolhas; }
    public void setQuantidadeFolhas(Integer quantidadeFolhas) { this.quantidadeFolhas = quantidadeFolhas; }

    public DificuldadeTecido getDificuldadeTecido() { return dificuldadeTecido; }
    public void setDificuldadeTecido(DificuldadeTecido dificuldadeTecido) { this.dificuldadeTecido = dificuldadeTecido; }

    public TipoTrajeto getTipoTrajeto() { return tipoTrajeto; }
    public void setTipoTrajeto(TipoTrajeto tipoTrajeto) { this.tipoTrajeto = tipoTrajeto; }

    public Integer getParadasForcadas() { return paradasForcadas; }
    public void setParadasForcadas(Integer paradasForcadas) { this.paradasForcadas = paradasForcadas; }
}
