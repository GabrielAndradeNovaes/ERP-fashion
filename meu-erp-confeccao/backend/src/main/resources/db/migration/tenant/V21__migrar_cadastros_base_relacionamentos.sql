-- V21: Migrar tabelas principais para usar IDs de relacionamentos das entidades base

-- 1. Produtos Base (categoria)
ALTER TABLE produtos_base RENAME COLUMN categoria TO categoria_legacy;
ALTER TABLE produtos_base ADD COLUMN categoria_id UUID REFERENCES categorias(id);

-- 2. Produtos SKUs (cor e tamanho)
ALTER TABLE produtos_skus RENAME COLUMN cor TO cor_legacy;
ALTER TABLE produtos_skus RENAME COLUMN tamanho TO tamanho_legacy;
ALTER TABLE produtos_skus ADD COLUMN cor_id UUID REFERENCES cores(id);
ALTER TABLE produtos_skus ADD COLUMN tamanho_id UUID REFERENCES tamanhos(id);

-- 3. Materiais (unidade_medida)
ALTER TABLE materiais RENAME COLUMN unidade_medida TO unidade_medida_legacy;
ALTER TABLE materiais ADD COLUMN unidade_medida_id UUID REFERENCES unidades_medida(id);

