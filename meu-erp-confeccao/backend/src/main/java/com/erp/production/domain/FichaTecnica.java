package com.erp.production.domain;

import com.erp.catalog.domain.ProdutoBase;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.UUID;

@Entity
@Table(name = "fichas_tecnicas")
public class FichaTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_base_id", nullable = false)
    private ProdutoBase produtoBase;

    @Column(nullable = false, length = 10)
    private String versao; // Ex: v1, v2

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "fichaTecnica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FichaTecnicaMaterial> materiais = new ArrayList<>();

    @OneToMany(mappedBy = "fichaTecnica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FichaTecnicaOperacao> operacoes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ProdutoBase getProdutoBase() {
        return produtoBase;
    }

    public void setProdutoBase(ProdutoBase produtoBase) {
        this.produtoBase = produtoBase;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<FichaTecnicaMaterial> getMateriais() {
        return materiais;
    }

    public void setMateriais(List<FichaTecnicaMaterial> materiais) {
        this.materiais = materiais;
    }

    public void addMaterial(FichaTecnicaMaterial material) {
        this.materiais.add(material);
        material.setFichaTecnica(this);
    }

    public void removeMaterial(FichaTecnicaMaterial material) {
        this.materiais.remove(material);
        material.setFichaTecnica(null);
    }

    public List<FichaTecnicaOperacao> getOperacoes() {
        return operacoes;
    }

    public void setOperacoes(List<FichaTecnicaOperacao> operacoes) {
        this.operacoes = operacoes;
    }

    public void addOperacao(FichaTecnicaOperacao operacao) {
        this.operacoes.add(operacao);
        operacao.setFichaTecnica(this);
    }

    public void removeOperacao(FichaTecnicaOperacao operacao) {
        this.operacoes.remove(operacao);
        operacao.setFichaTecnica(null);
    }

    public BigDecimal getTempoPadraoTotalCentesimal() {
        return operacoes.stream()
                .map(FichaTecnicaOperacao::getTempoCalculadoCentesimal)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}
