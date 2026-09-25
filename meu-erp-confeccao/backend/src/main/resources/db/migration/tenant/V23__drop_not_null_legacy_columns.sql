-- V22: Remover restrições NOT NULL das colunas legacy para permitir inserções pelas entidades refatoradas

ALTER TABLE produtos_skus ALTER COLUMN cor_legacy DROP NOT NULL;
ALTER TABLE produtos_skus ALTER COLUMN tamanho_legacy DROP NOT NULL;
ALTER TABLE produtos_base ALTER COLUMN categoria_legacy DROP NOT NULL;
ALTER TABLE materiais ALTER COLUMN unidade_medida_legacy DROP NOT NULL;
