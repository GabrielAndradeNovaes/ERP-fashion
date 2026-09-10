-- Remove as colunas antigas que baseavam o cálculo em faixas e tabelas estimadas
ALTER TABLE fichas_tecnicas_operacoes DROP COLUMN grau_dificuldade;
ALTER TABLE fichas_tecnicas_operacoes DROP COLUMN faixa_comprimento;

-- Adiciona os novos campos exatos para o cálculo do SAM
ALTER TABLE fichas_tecnicas_operacoes ADD COLUMN rpm_maquina INTEGER;
ALTER TABLE fichas_tecnicas_operacoes ADD COLUMN pontos_por_cm DECIMAL(10,2);
ALTER TABLE fichas_tecnicas_operacoes ADD COLUMN comprimento_costura_cm DECIMAL(10,2);
ALTER TABLE fichas_tecnicas_operacoes ADD COLUMN tipo_trajeto VARCHAR(30);
ALTER TABLE fichas_tecnicas_operacoes ADD COLUMN dificuldade_tecido VARCHAR(30);
ALTER TABLE fichas_tecnicas_operacoes ADD COLUMN sam_minutos DECIMAL(10,6);
