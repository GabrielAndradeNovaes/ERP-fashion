package com.erp.inventory.dto;

import com.erp.inventory.domain.TipoMovimentacao;

public class ProdutoMovimentacaoRequest {
    private TipoMovimentacao tipo;
    private Integer quantidade;
    private String documentoReferencia;

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getDocumentoReferencia() {
        return documentoReferencia;
    }

    public void setDocumentoReferencia(String documentoReferencia) {
        this.documentoReferencia = documentoReferencia;
    }
}
