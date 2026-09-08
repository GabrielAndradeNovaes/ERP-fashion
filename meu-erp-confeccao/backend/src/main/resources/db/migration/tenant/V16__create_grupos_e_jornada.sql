CREATE TABLE grupos_funcionarios (
    id UUID PRIMARY KEY,
    empresa_id UUID,
    nome VARCHAR(100) NOT NULL,
    CONSTRAINT uk_grupo_nome UNIQUE (empresa_id, nome)
);

ALTER TABLE funcionarios ADD COLUMN grupo_id UUID;
ALTER TABLE funcionarios ADD CONSTRAINT fk_funcionarios_grupo FOREIGN KEY (grupo_id) REFERENCES grupos_funcionarios(id);
ALTER TABLE funcionarios DROP COLUMN tempo_teorico;

CREATE TABLE funcionario_jornadas (
    id UUID PRIMARY KEY,
    funcionario_id UUID NOT NULL,
    dia_semana INTEGER NOT NULL, -- 1=Monday, 7=Sunday
    entrada TIME NOT NULL,
    saida TIME NOT NULL,
    CONSTRAINT fk_jornada_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id) ON DELETE CASCADE
);
