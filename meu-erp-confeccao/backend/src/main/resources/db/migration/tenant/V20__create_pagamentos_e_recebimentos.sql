CREATE TABLE IF NOT EXISTS financeiro_titulos_receber (
    id UUID PRIMARY KEY,
    empresa_id UUID NOT NULL,
    cliente_id UUID,
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(15,2) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_emissao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL, -- PENDING, PAID, CANCELED
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP
);

CREATE TABLE IF NOT EXISTS financeiro_transacoes_pagamento (
    id UUID PRIMARY KEY,
    titulo_id UUID NOT NULL,
    gateway VARCHAR(50) NOT NULL, -- MOCK, MERCADO_PAGO, ASAAS
    gateway_transacao_id VARCHAR(100),
    status VARCHAR(50) NOT NULL, -- PENDING, PROCESSING, PAID, FAILED, REFUNDED
    metodo_pagamento VARCHAR(50) NOT NULL, -- PIX, BOLETO, CREDIT_CARD
    qr_code_payload TEXT,
    qr_code_image_url TEXT,
    boleto_url TEXT,
    boleto_linha_digitavel TEXT,
    webhook_payload JSONB,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    CONSTRAINT fk_transacao_titulo FOREIGN KEY (titulo_id) REFERENCES financeiro_titulos_receber(id) ON DELETE CASCADE
);
