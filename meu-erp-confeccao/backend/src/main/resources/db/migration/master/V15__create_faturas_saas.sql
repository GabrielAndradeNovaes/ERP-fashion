CREATE TABLE master.faturas_saas (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(19,4) NOT NULL,
    data_vencimento DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES master.clientes_tenant(id)
);

CREATE TABLE master.faturas_transacoes (
    id UUID PRIMARY KEY,
    fatura_saas_id UUID NOT NULL,
    gateway VARCHAR(50) NOT NULL,
    gateway_transacao_id VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    qr_code_payload TEXT,
    qr_code_image_url TEXT,
    payload_resposta TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fatura_saas_id) REFERENCES master.faturas_saas(id)
);
