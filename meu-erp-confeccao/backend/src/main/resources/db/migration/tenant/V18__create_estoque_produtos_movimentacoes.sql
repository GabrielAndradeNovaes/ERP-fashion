CREATE TABLE estoque_produtos_movimentacoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_sku_id UUID NOT NULL REFERENCES produtos_skus(id),
    tipo VARCHAR(20) NOT NULL,
    quantidade INTEGER NOT NULL,
    documento_referencia VARCHAR(100),
    data_movimentacao TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000'
);
