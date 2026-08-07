package com.erp.core.dto;

public class DashboardResumoDTO {
    private long totalProdutos;
    private long opsEmAndamento;
    private long opsConcluidas;
    private double valorTotalEstoque;

    public DashboardResumoDTO() {}

    public DashboardResumoDTO(long totalProdutos, long opsEmAndamento, long opsConcluidas, double valorTotalEstoque) {
        this.totalProdutos = totalProdutos;
        this.opsEmAndamento = opsEmAndamento;
        this.opsConcluidas = opsConcluidas;
        this.valorTotalEstoque = valorTotalEstoque;
    }

    public long getTotalProdutos() { return totalProdutos; }
    public void setTotalProdutos(long totalProdutos) { this.totalProdutos = totalProdutos; }

    public long getOpsEmAndamento() { return opsEmAndamento; }
    public void setOpsEmAndamento(long opsEmAndamento) { this.opsEmAndamento = opsEmAndamento; }

    public long getOpsConcluidas() { return opsConcluidas; }
    public void setOpsConcluidas(long opsConcluidas) { this.opsConcluidas = opsConcluidas; }

    public double getValorTotalEstoque() { return valorTotalEstoque; }
    public void setValorTotalEstoque(double valorTotalEstoque) { this.valorTotalEstoque = valorTotalEstoque; }
}
