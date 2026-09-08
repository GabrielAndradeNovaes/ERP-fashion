ALTER TABLE apontamentos ADD COLUMN IF NOT EXISTS pago boolean default false not null;
ALTER TABLE apontamentos ADD COLUMN IF NOT EXISTS data_pagamento timestamp;

CREATE TABLE IF NOT EXISTS titulos_pagar (
    id uuid not null primary key,
    data_emissao date not null,
    data_pagamento date,
    data_vencimento date not null,
    descricao varchar(200) not null,
    status varchar(20) not null check (status in ('PENDENTE','PAGO','CANCELADO')),
    valor numeric(12,2) not null,
    empresa_id uuid not null references empresas(id),
    funcionario_id uuid references funcionarios(id)
);
