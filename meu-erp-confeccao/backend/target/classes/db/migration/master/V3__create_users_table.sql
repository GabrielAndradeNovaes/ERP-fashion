CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_tenant FOREIGN KEY (tenant_id) REFERENCES clientes_tenant(schema_name)
);

-- Inserir usuário de exemplo
-- Senha: password123 (hash BCrypt: $2a$10$Ew.HlZ5uT13JzB5O92TDbOsP5p6/wT/fGqYwV.T3C8Wn23Y3r2bRy)
INSERT INTO usuarios (id, tenant_id, nome, email, senha, role, ativo, criado_em) 
VALUES (
    gen_random_uuid(),
    'tenant_1', 
    'Admin Fashion', 
    'admin@fashion.com', 
    '$2a$06$4U6a6JibN3LpZsWKWGunh.bBDLjHwh4dJjRlkKyCUnaDuy2IsWj8K', 
    'ADMIN',
    true,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO NOTHING;
