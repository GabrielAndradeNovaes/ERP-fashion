CREATE TABLE materiais_fornecedor (
    id UUID PRIMARY KEY,
    empresa_id UUID NOT NULL,
    fornecedor_id UUID NOT NULL,
    material_id UUID NOT NULL,
    codigo_fornecedor VARCHAR(100) NOT NULL,
    descricao_fornecedor VARCHAR(255),
    fator_conversao NUMERIC(10, 4) DEFAULT 1.0,
    CONSTRAINT fk_matforn_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id),
    CONSTRAINT fk_matforn_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedores(id),
    CONSTRAINT fk_matforn_material FOREIGN KEY (material_id) REFERENCES materiais(id),
    CONSTRAINT uk_fornecedor_codigo UNIQUE (fornecedor_id, codigo_fornecedor)
);
