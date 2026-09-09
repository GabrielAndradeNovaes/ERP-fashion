CREATE TABLE IF NOT EXISTS apontamentos_manuais (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID REFERENCES empresas(id),
    funcionario_id UUID NOT NULL REFERENCES funcionarios(id),
    data_hora TIMESTAMP NOT NULL,
    minutos INTEGER NOT NULL,
    observacao VARCHAR(500),
    pago BOOLEAN NOT NULL DEFAULT false,
    data_pagamento TIMESTAMP
);
