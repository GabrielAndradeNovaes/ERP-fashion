CREATE TABLE tabela_tempo_padrao (
    id UUID PRIMARY KEY,
    indice NUMERIC(10,2) NOT NULL,
    grau_dificuldade VARCHAR(50) NOT NULL,
    faixa_comprimento VARCHAR(50) NOT NULL,
    tempo_centesimal NUMERIC(10,4) NOT NULL,
    CONSTRAINT uk_tabela_tempo_padrao UNIQUE (indice, grau_dificuldade, faixa_comprimento)
);
