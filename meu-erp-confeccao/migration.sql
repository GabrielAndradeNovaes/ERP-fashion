ALTER TABLE master.materiais ADD COLUMN quantidade_atual NUMERIC(19,4) DEFAULT 0;
ALTER TABLE tenant_1.materiais ADD COLUMN quantidade_atual NUMERIC(19,4) DEFAULT 0;

CREATE TABLE master.ordens_producao (
    id UUID PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    produto_base_id UUID NOT NULL,
    ficha_tecnica_id UUID NOT NULL,
    quantidade INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    data_inicio TIMESTAMP,
    CONSTRAINT fk_op_produto FOREIGN KEY (produto_base_id) REFERENCES master.produtos_base(id),
    CONSTRAINT fk_op_ficha FOREIGN KEY (ficha_tecnica_id) REFERENCES master.fichas_tecnicas(id)
);

CREATE TABLE tenant_1.ordens_producao (
    id UUID PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    produto_base_id UUID NOT NULL,
    ficha_tecnica_id UUID NOT NULL,
    quantidade INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    data_inicio TIMESTAMP,
    CONSTRAINT fk_op_produto FOREIGN KEY (produto_base_id) REFERENCES tenant_1.produtos_base(id),
    CONSTRAINT fk_op_ficha FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_1.fichas_tecnicas(id)
);
