CREATE TABLE departamentos (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    empresa_id UUID REFERENCES master.empresas(id)
);

CREATE TABLE localizacoes (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(50),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    empresa_id UUID REFERENCES master.empresas(id)
);
