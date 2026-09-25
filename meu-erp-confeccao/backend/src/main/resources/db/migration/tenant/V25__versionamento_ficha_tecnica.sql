ALTER TABLE fichas_tecnicas DROP CONSTRAINT IF EXISTS fichas_tecnicas_produto_base_id_key;
ALTER TABLE fichas_tecnicas ADD COLUMN ativa BOOLEAN DEFAULT TRUE;
