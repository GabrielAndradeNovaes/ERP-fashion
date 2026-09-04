DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_schema = 'master' AND table_name = 'usuarios' AND column_name = 'filial_principal_id') THEN
        ALTER TABLE master.usuarios ADD COLUMN filial_principal_id UUID;
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS master.usuario_permissoes (
    usuario_id UUID NOT NULL,
    permissao VARCHAR(100) NOT NULL,
    CONSTRAINT fk_usuario_permissoes_usuario FOREIGN KEY (usuario_id) REFERENCES master.usuarios(id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, permissao)
);
