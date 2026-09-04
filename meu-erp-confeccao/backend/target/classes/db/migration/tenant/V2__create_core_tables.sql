-- Tabela de Clientes
CREATE TABLE IF NOT EXISTS clientes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    documento VARCHAR(255),
    email VARCHAR(255),
    telefone VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false
);

-- Tabela de Fornecedores
CREATE TABLE IF NOT EXISTS fornecedores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    documento VARCHAR(255),
    email VARCHAR(255),
    telefone VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false
);

-- Tabela de Categorias
CREATE TABLE IF NOT EXISTS categorias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    tipo VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false
);

-- Tabela de Unidades de Medida
CREATE TABLE IF NOT EXISTS unidades_medida (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sigla VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false
);
