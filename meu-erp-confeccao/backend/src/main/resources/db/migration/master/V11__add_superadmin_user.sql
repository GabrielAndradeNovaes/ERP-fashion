-- Insere o tenant master caso não exista para satisfazer a chave estrangeira
INSERT INTO clientes_tenant (nome_empresa, schema_name) 
VALUES ('Administração Control Plane', 'master') 
ON CONFLICT (schema_name) DO NOTHING;

-- Insere ou atualiza o usuario admin@gmail.com para SUPERADMIN
INSERT INTO usuarios (id, tenant_id, nome, email, senha, role, ativo, criado_em)
VALUES (
    gen_random_uuid(),
    'master', 
    'Super Admin', 
    'admin@gmail.com', 
    '$2a$06$vvPXdhlTH1AvjdBWhhGEFOPXspGogSGezlm81DYVspmOBIT78aXGq', 
    'SUPERADMIN',
    true,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO UPDATE 
SET senha = EXCLUDED.senha, role = 'SUPERADMIN', tenant_id = 'master';

-- Remove SUPERADMIN de qualquer outro usuário
UPDATE usuarios 
SET role = 'ADMIN' 
WHERE role = 'SUPERADMIN' AND email != 'admin@gmail.com';
