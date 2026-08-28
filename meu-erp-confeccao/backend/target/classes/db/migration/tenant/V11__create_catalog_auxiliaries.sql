-- Tabela de Cores
CREATE TABLE cores (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    codigo_hex VARCHAR(20),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    empresa_id UUID,
    CONSTRAINT fk_cores_empresa FOREIGN KEY (empresa_id) REFERENCES empresas (id)
);

-- Tabela de Tamanhos
CREATE TABLE tamanhos (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    sigla VARCHAR(20),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    empresa_id UUID,
    CONSTRAINT fk_tamanhos_empresa FOREIGN KEY (empresa_id) REFERENCES empresas (id)
);

-- Tabela de Unidades de Medida
CREATE TABLE unidades_medida (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    sigla VARCHAR(20) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    empresa_id UUID,
    CONSTRAINT fk_unidades_medida_empresa FOREIGN KEY (empresa_id) REFERENCES empresas (id)
);
