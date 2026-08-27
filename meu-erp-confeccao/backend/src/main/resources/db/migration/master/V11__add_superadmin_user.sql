-- Insere ou atualiza o usuario admin@gmail.com para SUPERADMIN
INSERT INTO usuarios (id, tenant_id, nome, email, senha, role, ativo, criado_em)
VALUES (
    gen_random_uuid(),
    'master', 
    'Super Admin', 
    'admin@gmail.com', 
    '$2a$10$qa5NrjWLAm.8YZoqI/pHY.bLL8vkaN0xm119Mkxfvg5fV0ifh53ze', 
    'SUPERADMIN',
    true,
    CURRENT_TIMESTAMP
) ON CONFLICT (email) DO UPDATE 
SET senha = EXCLUDED.senha, role = 'SUPERADMIN', tenant_id = 'master';

-- Remove SUPERADMIN de qualquer outro usuário
UPDATE usuarios 
SET role = 'ADMIN' 
WHERE role = 'SUPERADMIN' AND email != 'admin@gmail.com';
