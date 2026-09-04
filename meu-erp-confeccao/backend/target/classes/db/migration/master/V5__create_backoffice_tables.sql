-- Atualizando a tabela clientes_tenant existente
ALTER TABLE clientes_tenant
ADD COLUMN cnpj VARCHAR(20),
ADD COLUMN razao_social VARCHAR(255),
ADD COLUMN contato_nome VARCHAR(255),
ADD COLUMN contato_telefone VARCHAR(20);

-- Removendo coluna 'ativo' antiga e substituindo por 'status'
ALTER TABLE clientes_tenant DROP COLUMN IF EXISTS ativo;
ALTER TABLE clientes_tenant ADD COLUMN status VARCHAR(20) DEFAULT 'ATIVO' NOT NULL;
-- Valores possíveis para status: 'ATIVO', 'INADIMPLENTE', 'CANCELADO'

-- Tabela de Módulos Contratados (Feature Flags)
CREATE TABLE IF NOT EXISTS tenant_modules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    module_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tenant_modules_tenant FOREIGN KEY (tenant_id) REFERENCES clientes_tenant(schema_name),
    CONSTRAINT uk_tenant_module UNIQUE (tenant_id, module_name)
);

-- Tabela de Contratos (Assinaturas)
CREATE TABLE IF NOT EXISTS tenant_contracts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) NOT NULL,
    valor_mensal DECIMAL(10, 2) NOT NULL,
    data_inicio DATE NOT NULL,
    data_cancelamento DATE,
    status_contrato VARCHAR(20) DEFAULT 'ATIVO' NOT NULL, -- ATIVO, CANCELADO
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tenant_contracts_tenant FOREIGN KEY (tenant_id) REFERENCES clientes_tenant(schema_name)
);

-- Tabela de Faturas (Mensalidades)
CREATE TABLE IF NOT EXISTS tenant_invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contract_id UUID NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    valor DECIMAL(10, 2) NOT NULL,
    status_fatura VARCHAR(20) DEFAULT 'PENDENTE' NOT NULL, -- PENDENTE, PAGA, ATRASADA, CANCELADA
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tenant_invoices_contract FOREIGN KEY (contract_id) REFERENCES tenant_contracts(id)
);
