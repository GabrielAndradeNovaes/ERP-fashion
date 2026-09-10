package com.erp.production.domain;

public enum DificuldadeTecido {
    NORMAL(1.00),
    MALHA(1.10),
    ESCORREGADIO(1.20),
    LISTRADO_XADREZ(1.30);

    private final double multiplicador;

    DificuldadeTecido(double multiplicador) {
        this.multiplicador = multiplicador;
    }

    public double getMultiplicador() {
        return multiplicador;
    }
}
