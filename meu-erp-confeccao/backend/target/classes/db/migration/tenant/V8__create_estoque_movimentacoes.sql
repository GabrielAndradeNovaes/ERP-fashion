CREATE TABLE estoque_movimentacoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    material_id UUID NOT NULL REFERENCES materiais(id),
    tipo VARCHAR(20) NOT NULL,
    quantidade NUMERIC(19, 4) NOT NULL,
    documento_referencia VARCHAR(100),
    data_movimentacao TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
