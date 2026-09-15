CREATE TABLE IF NOT EXISTS acessos_logs (
    id uuid not null primary key,
    tenant_id uuid not null references clientes_tenant(id),
    usuario_id uuid not null references usuarios(id),
    data_acesso timestamp not null,
    ip varchar(50),
    user_agent varchar(255)
);
