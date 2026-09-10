package com.erp.production.domain;

import com.erp.core.domain.Empresa;
import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fichas_tecnicas_operacoes")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class FichaTecnicaOperacao {

    @jakarta.persistence.ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @jakarta.persistence.JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ficha_tecnica_id", nullable = false)
    private FichaTecnica fichaTecnica;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 50)
    private String maquina;

    @Column(name = "ordem_execucao", nullable = false)
    private Integer ordemExecucao;

    @Column(name = "quantidade_folhas", nullable = false)
    private Integer quantidadeFolhas;

    @Column(name = "quantidade_paradas", nullable = false)
    private Integer quantidadeParadas;

    @Column(name = "rpm_maquina")
    private Integer rpmMaquina;

    @Column(name = "pontos_por_cm", precision = 10, scale = 2)
    private BigDecimal pontosPorCm;

    @Column(name = "comprimento_costura_cm", precision = 10, scale = 2)
    private BigDecimal comprimentoCosturaCm;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_trajeto", length = 30)
    private TipoTrajeto tipoTrajeto;

    @Enumerated(EnumType.STRING)
    @Column(name = "dificuldade_tecido", length = 30)
    private DificuldadeTecido dificuldadeTecido;

    @Column(name = "tempo_calculado_centesimal", precision = 10, scale = 2)
    private BigDecimal tempoCalculadoCentesimal; // Keep as fallback/old if needed

    @Column(name = "sam_minutos", precision = 10, scale = 6)
    private BigDecimal samMinutos;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public FichaTecnica getFichaTecnica() { return fichaTecnica; }
    public void setFichaTecnica(FichaTecnica fichaTecnica) { this.fichaTecnica = fichaTecnica; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getMaquina() { return maquina; }
    public void setMaquina(String maquina) { this.maquina = maquina; }
    public Integer getOrdemExecucao() { return ordemExecucao; }
    public void setOrdemExecucao(Integer ordemExecucao) { this.ordemExecucao = ordemExecucao; }
    public Integer getQuantidadeFolhas() { return quantidadeFolhas; }
    public void setQuantidadeFolhas(Integer quantidadeFolhas) { this.quantidadeFolhas = quantidadeFolhas; }
    public Integer getQuantidadeParadas() { return quantidadeParadas; }
    public void setQuantidadeParadas(Integer quantidadeParadas) { this.quantidadeParadas = quantidadeParadas; }
    public BigDecimal getTempoCalculadoCentesimal() { return tempoCalculadoCentesimal; }
    public void setTempoCalculadoCentesimal(BigDecimal tempoCalculadoCentesimal) { this.tempoCalculadoCentesimal = tempoCalculadoCentesimal; }
    public Integer getRpmMaquina() { return rpmMaquina; }
    public void setRpmMaquina(Integer rpmMaquina) { this.rpmMaquina = rpmMaquina; }
    public BigDecimal getPontosPorCm() { return pontosPorCm; }
    public void setPontosPorCm(BigDecimal pontosPorCm) { this.pontosPorCm = pontosPorCm; }
    public BigDecimal getComprimentoCosturaCm() { return comprimentoCosturaCm; }
    public void setComprimentoCosturaCm(BigDecimal comprimentoCosturaCm) { this.comprimentoCosturaCm = comprimentoCosturaCm; }
    public TipoTrajeto getTipoTrajeto() { return tipoTrajeto; }
    public void setTipoTrajeto(TipoTrajeto tipoTrajeto) { this.tipoTrajeto = tipoTrajeto; }
    public DificuldadeTecido getDificuldadeTecido() { return dificuldadeTecido; }
    public void setDificuldadeTecido(DificuldadeTecido dificuldadeTecido) { this.dificuldadeTecido = dificuldadeTecido; }
    public BigDecimal getSamMinutos() { return samMinutos; }
    public void setSamMinutos(BigDecimal samMinutos) { this.samMinutos = samMinutos; }
}
