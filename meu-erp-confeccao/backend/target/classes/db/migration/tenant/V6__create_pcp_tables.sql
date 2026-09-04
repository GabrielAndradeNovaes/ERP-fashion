-- Tabela de Funcionários (Chão de Fábrica)
CREATE TABLE IF NOT EXISTS funcionarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(150) NOT NULL,
    matricula VARCHAR(50) NOT NULL UNIQUE,
    carga_horaria_diaria_padrao DECIMAL(5,2) NOT NULL,
    carga_horaria_mensal_padrao DECIMAL(5,2) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true
);

-- Tabela de Itens da Ordem de Produção (Grade de SKUs)
CREATE TABLE IF NOT EXISTS ordens_producao_itens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ordem_producao_id UUID NOT NULL REFERENCES ordens_producao(id),
    produto_sku_id UUID NOT NULL REFERENCES produtos_skus(id),
    quantidade INTEGER NOT NULL,
    UNIQUE(ordem_producao_id, produto_sku_id)
);

-- Tabela de Pacotes (Lotes transitando na fábrica)
CREATE TABLE IF NOT EXISTS pacotes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ordem_producao_id UUID NOT NULL REFERENCES ordens_producao(id),
    produto_sku_id UUID NOT NULL REFERENCES produtos_skus(id),
    sequencial INTEGER NOT NULL,
    quantidade_pecas INTEGER NOT NULL,
    UNIQUE(ordem_producao_id, produto_sku_id, sequencial)
);

-- Tabela de Cupons (Tickets para Bipagem)
CREATE TABLE IF NOT EXISTS cupons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pacote_id UUID NOT NULL REFERENCES pacotes(id),
    operacao_id UUID NOT NULL REFERENCES fichas_tecnicas_operacoes(id),
    codigo_barras VARCHAR(100) NOT NULL UNIQUE,
    tempo_total_centesimal DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
);

-- Tabela de Apontamentos (Registro da Bipagem)
CREATE TABLE IF NOT EXISTS apontamentos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cupom_id UUID NOT NULL UNIQUE REFERENCES cupons(id),
    funcionario_id UUID NOT NULL REFERENCES funcionarios(id),
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Ocorrências (Downtime)
CREATE TABLE IF NOT EXISTS ocorrencias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    funcionario_id UUID NOT NULL REFERENCES funcionarios(id),
    motivo VARCHAR(255) NOT NULL,
    tempo_descontado_centesimal DECIMAL(5,2) NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
