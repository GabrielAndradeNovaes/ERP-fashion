ALTER TABLE master.produtos_skus ADD COLUMN quantidade_atual INT DEFAULT 0 NOT NULL;
ALTER TABLE tenant_1.produtos_skus ADD COLUMN quantidade_atual INT DEFAULT 0 NOT NULL;

-- Remove duplicatas temporariamente se existirem, mas assumimos que o banco está limpo ou tem dados ok.
ALTER TABLE master.fichas_tecnicas ADD CONSTRAINT uk_master_ficha_produto UNIQUE (produto_base_id);
ALTER TABLE tenant_1.fichas_tecnicas ADD CONSTRAINT uk_t1_ficha_produto UNIQUE (produto_base_id);

CREATE TABLE master.estoque_produtos_movimentacoes (
    id UUID PRIMARY KEY,
    produto_sku_id UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    quantidade INT NOT NULL,
    documento_referencia VARCHAR(100),
    data_movimentacao TIMESTAMP NOT NULL,
    CONSTRAINT fk_master_mov_sku FOREIGN KEY (produto_sku_id) REFERENCES master.produtos_skus(id)
);

CREATE TABLE tenant_1.estoque_produtos_movimentacoes (
    id UUID PRIMARY KEY,
    produto_sku_id UUID NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    quantidade INT NOT NULL,
    documento_referencia VARCHAR(100),
    data_movimentacao TIMESTAMP NOT NULL,
    CONSTRAINT fk_t1_mov_sku FOREIGN KEY (produto_sku_id) REFERENCES tenant_1.produtos_skus(id)
);
