CREATE TABLE IF NOT EXISTS clientes (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    documento VARCHAR(255),
    email VARCHAR(255),
    telefone VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS fornecedores (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    documento VARCHAR(255),
    email VARCHAR(255),
    telefone VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS categorias (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS unidades_medida (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    sigla VARCHAR(50) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
