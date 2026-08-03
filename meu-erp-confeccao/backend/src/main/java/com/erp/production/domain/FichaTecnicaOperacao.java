package com.erp.production.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fichas_tecnicas_operacoes")
public class FichaTecnicaOperacao {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "grau_dificuldade", nullable = false, length = 30)
    private GrauDificuldade grauDificuldade;

    @Enumerated(EnumType.STRING)
    @Column(name = "faixa_comprimento", nullable = false, length = 30)
    private FaixaComprimentoCostura faixaComprimento;

    @Column(name = "tempo_calculado_centesimal", nullable = false, precision = 10, scale = 2)
    private BigDecimal tempoCalculadoCentesimal;

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
    public GrauDificuldade getGrauDificuldade() { return grauDificuldade; }
    public void setGrauDificuldade(GrauDificuldade grauDificuldade) { this.grauDificuldade = grauDificuldade; }
    public FaixaComprimentoCostura getFaixaComprimento() { return faixaComprimento; }
    public void setFaixaComprimento(FaixaComprimentoCostura faixaComprimento) { this.faixaComprimento = faixaComprimento; }
    public BigDecimal getTempoCalculadoCentesimal() { return tempoCalculadoCentesimal; }
    public void setTempoCalculadoCentesimal(BigDecimal tempoCalculadoCentesimal) { this.tempoCalculadoCentesimal = tempoCalculadoCentesimal; }
}
