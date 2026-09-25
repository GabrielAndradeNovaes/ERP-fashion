-- Adiciona coluna ativo a unidades_medida
ALTER TABLE unidades_medida ADD COLUMN IF NOT EXISTS ativo BOOLEAN NOT NULL DEFAULT true;
