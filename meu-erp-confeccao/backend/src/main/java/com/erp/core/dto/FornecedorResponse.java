package com.erp.core.dto;

import java.util.UUID;

public record FornecedorResponse(
    UUID id,
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
