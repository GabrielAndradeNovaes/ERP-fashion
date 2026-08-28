package com.erp.core.dto;

public record FornecedorRequest(
    String nome,
    String documento,
    String email,
    String telefone,
    String tipoPessoa,
    String razaoSocial,
    String inscricaoEstadual,
    String categoriaFornecedor,
    Integer prazoPagamentoPadrao,
    String contatoNome,
    String status,
    String endereco
) {}
