-- Conceder módulo de COMPRAS para todos os tenants existentes
INSERT INTO master.tenant_modules (tenant_id, module_name, is_active)
SELECT schema_name, 'COMPRAS', true
FROM master.clientes_tenant
ON CONFLICT (tenant_id, module_name) DO NOTHING;
