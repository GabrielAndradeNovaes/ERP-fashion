CREATE TABLE IF NOT EXISTS titulos_receber (
    id uuid not null primary key,
    data_emissao date not null,
    data_pagamento date,
    data_vencimento date not null,
    descricao varchar(200) not null,
    status varchar(20) not null check (status in ('PENDENTE','RECEBIDO','CANCELADO')),
    valor numeric(12,2) not null,
    empresa_id uuid not null references empresas(id),
    cliente_id uuid references clientes(id)
);
