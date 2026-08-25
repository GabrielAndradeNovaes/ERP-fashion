CREATE TABLE materiais (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    unidade_medida VARCHAR(20) NOT NULL,
    custo_unitario NUMERIC(10, 2),
    quantidade_atual NUMERIC(19, 4) DEFAULT 0,
    criado_em TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE fichas_tecnicas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_base_id UUID NOT NULL UNIQUE REFERENCES produtos_base(id),
    versao VARCHAR(10) NOT NULL,
    observacoes TEXT,
    criado_em TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE fichas_tecnicas_materiais (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ficha_tecnica_id UUID NOT NULL REFERENCES fichas_tecnicas(id),
    material_id UUID NOT NULL REFERENCES materiais(id),
    quantidade NUMERIC(10, 4) NOT NULL
);

CREATE TABLE fichas_tecnicas_operacoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ficha_tecnica_id UUID NOT NULL REFERENCES fichas_tecnicas(id),
    nome VARCHAR(100) NOT NULL,
    maquina VARCHAR(50),
    ordem_execucao INTEGER NOT NULL,
    quantidade_folhas INTEGER NOT NULL,
    quantidade_paradas INTEGER NOT NULL,
    grau_dificuldade VARCHAR(30) NOT NULL,
    faixa_comprimento VARCHAR(30) NOT NULL,
    tempo_calculado_centesimal NUMERIC(10, 2) NOT NULL
);

CREATE TABLE ordens_producao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero VARCHAR(50) NOT NULL UNIQUE,
    produto_base_id UUID NOT NULL REFERENCES produtos_base(id),
    ficha_tecnica_id UUID NOT NULL REFERENCES fichas_tecnicas(id),
    quantidade INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    data_inicio TIMESTAMP WITHOUT TIME ZONE
);
