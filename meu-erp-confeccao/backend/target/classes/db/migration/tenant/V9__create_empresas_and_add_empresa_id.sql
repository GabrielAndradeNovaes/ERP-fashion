CREATE TABLE IF NOT EXISTS empresas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_fantasia VARCHAR(255) NOT NULL,
    razao_social VARCHAR(255),
    cnpj VARCHAR(20),
    ativo BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS usuario_empresas (
    usuario_id UUID NOT NULL,
    empresa_id UUID NOT NULL REFERENCES empresas(id),
    PRIMARY KEY (usuario_id, empresa_id)
);

-- Insere empresa padrao para os dados ja existentes
INSERT INTO empresas (id, nome_fantasia, ativo) VALUES ('00000000-0000-0000-0000-000000000000', 'Matriz (Padrão)', true);

-- Adicionando empresa_id em todas as tabelas (Cadastros Básicos)
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE fornecedores ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE categorias ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE unidades_medida ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';

-- Produtos e Materiais
ALTER TABLE produtos_base ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE produtos_skus ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE materiais ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE fichas_tecnicas ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE fichas_tecnicas_materiais ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE fichas_tecnicas_operacoes ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';

-- Produção (PCP)
ALTER TABLE ordens_producao ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE ordens_producao_itens ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE pacotes ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE cupons ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE apontamentos ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE ocorrencias ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT '00000000-0000-0000-0000-000000000000';

-- Estoque
DO $$
BEGIN
    IF EXISTS (SELECT FROM pg_tables WHERE schemaname = current_schema() AND tablename = 'estoque_produtos_movimentacoes') THEN
        EXECUTE 'ALTER TABLE estoque_produtos_movimentacoes ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT ''00000000-0000-0000-0000-000000000000''';
    END IF;
    
    IF EXISTS (SELECT FROM pg_tables WHERE schemaname = current_schema() AND tablename = 'estoque_materiais_movimentacoes') THEN
        EXECUTE 'ALTER TABLE estoque_materiais_movimentacoes ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT ''00000000-0000-0000-0000-000000000000''';
    END IF;
    
    IF EXISTS (SELECT FROM pg_tables WHERE schemaname = current_schema() AND tablename = 'estoque_movimentacoes') THEN
        EXECUTE 'ALTER TABLE estoque_movimentacoes ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT ''00000000-0000-0000-0000-000000000000''';
    END IF;
END $$;

-- Tabela de Tempo (Se existir, usando DO block para checar)
DO $$
BEGIN
    IF EXISTS (SELECT FROM pg_tables WHERE schemaname = current_schema() AND tablename = 'tabela_tempo_padrao') THEN
        EXECUTE 'ALTER TABLE tabela_tempo_padrao ADD COLUMN IF NOT EXISTS empresa_id UUID REFERENCES empresas(id) DEFAULT ''00000000-0000-0000-0000-000000000000''';
    END IF;
END $$;
