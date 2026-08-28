-- Adiciona a coluna slug
ALTER TABLE master.clientes_tenant ADD COLUMN slug VARCHAR(100);

-- Preenche o slug baseado no nome da empresa para os registros existentes
UPDATE master.clientes_tenant 
SET slug = LOWER(REGEXP_REPLACE(
    translate(nome_empresa, 'áéíóúâêîôûãõçÁÉÍÓÚÂÊÎÔÛÃÕÇ', 'aeiouaeiouaocAEIOUAEIOUAOC'), 
    '[^a-zA-Z0-9]+', '-', 'g'
))
WHERE slug IS NULL;

-- Remove eventuais traços no começo e no fim do slug gerado
UPDATE master.clientes_tenant SET slug = TRIM(BOTH '-' FROM slug);

-- Garante que o slug nao seja nulo e seja unico
ALTER TABLE master.clientes_tenant ALTER COLUMN slug SET NOT NULL;
ALTER TABLE master.clientes_tenant ADD CONSTRAINT uk_clientes_tenant_slug UNIQUE (slug);
