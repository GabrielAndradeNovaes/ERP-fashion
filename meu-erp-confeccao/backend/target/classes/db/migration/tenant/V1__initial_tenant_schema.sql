-- Tabela de Produtos Base (Catálogo)
CREATE TABLE IF NOT EXISTS produtos_base (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de SKUs (Produto + Cor + Tamanho)
CREATE TABLE IF NOT EXISTS produtos_skus (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_base_id UUID NOT NULL REFERENCES produtos_base(id),
    cor VARCHAR(50) NOT NULL,
    tamanho VARCHAR(20) NOT NULL,
    codigo_barras VARCHAR(100) UNIQUE,
    preco_venda DECIMAL(10, 2),
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(produto_base_id, cor, tamanho)
);
