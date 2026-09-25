package com.erp.procurement.domain;

import com.erp.core.domain.Empresa;
import com.erp.core.domain.Fornecedor;
import org.hibernate.annotations.Filter;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "recebimentos_nf")
@Filter(name = "empresaFilter", condition = "empresa_id IN (:empresaIds)")
public class RecebimentoNf {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id")
    private Fornecedor fornecedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_compra_id")
    private OrdemCompra ordemCompra;

    @Column(name = "chave_acesso_nfe", length = 100)
    private String chaveAcessoNfe;

    @Column(name = "numero_nfe", length = 50)
    private String numeroNfe;

    @Column(name = "data_emissao")
    private LocalDateTime dataEmissao;

    @Column(name = "data_recebimento", nullable = false)
    private LocalDateTime dataRecebimento;

    @Column(name = "xml_conteudo", columnDefinition = "TEXT")
    private String xmlConteudo;

    @Column(name = "valor_total_nf")
    private BigDecimal valorTotalNf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private RecebimentoNfStatus status;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "recebimentoNf", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecebimentoNfItem> itens = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now();
        }
        if (this.dataRecebimento == null) {
            this.dataRecebimento = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = RecebimentoNfStatus.PENDENTE;
        }
        if (this.valorTotalNf == null) {
            this.valorTotalNf = BigDecimal.ZERO;
        }
    }

    public void addItem(RecebimentoNfItem item) {
        itens.add(item);
        item.setRecebimentoNf(this);
    }

    public void removeItem(RecebimentoNfItem item) {
        itens.remove(item);
        item.setRecebimentoNf(null);
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }
    public OrdemCompra getOrdemCompra() { return ordemCompra; }
    public void setOrdemCompra(OrdemCompra ordemCompra) { this.ordemCompra = ordemCompra; }
    public String getChaveAcessoNfe() { return chaveAcessoNfe; }
    public void setChaveAcessoNfe(String chaveAcessoNfe) { this.chaveAcessoNfe = chaveAcessoNfe; }
    public String getNumeroNfe() { return numeroNfe; }
    public void setNumeroNfe(String numeroNfe) { this.numeroNfe = numeroNfe; }
    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDateTime dataEmissao) { this.dataEmissao = dataEmissao; }
    public LocalDateTime getDataRecebimento() { return dataRecebimento; }
    public void setDataRecebimento(LocalDateTime dataRecebimento) { this.dataRecebimento = dataRecebimento; }
    public String getXmlConteudo() { return xmlConteudo; }
    public void setXmlConteudo(String xmlConteudo) { this.xmlConteudo = xmlConteudo; }
    public BigDecimal getValorTotalNf() { return valorTotalNf; }
    public void setValorTotalNf(BigDecimal valorTotalNf) { this.valorTotalNf = valorTotalNf; }
    public RecebimentoNfStatus getStatus() { return status; }
    public void setStatus(RecebimentoNfStatus status) { this.status = status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public List<RecebimentoNfItem> getItens() { return itens; }
    public void setItens(List<RecebimentoNfItem> itens) { this.itens = itens; }
}
