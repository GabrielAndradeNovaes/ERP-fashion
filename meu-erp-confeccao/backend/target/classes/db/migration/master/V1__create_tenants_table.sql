CREATE TABLE IF NOT EXISTS clientes_tenant (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_empresa VARCHAR(255) NOT NULL,
    schema_name VARCHAR(50) NOT NULL UNIQUE,
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inserir um tenant de exemplo para desenvolvimento
INSERT INTO clientes_tenant (nome_empresa, schema_name) 
VALUES ('Confecção Exemplo S/A', 'tenant_1') 
ON CONFLICT (schema_name) DO NOTHING;
