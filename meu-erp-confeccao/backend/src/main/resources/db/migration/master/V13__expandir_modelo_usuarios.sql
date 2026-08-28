-- V13__expandir_modelo_usuarios.sql
-- Expansão de Usuários
ALTER TABLE master.usuarios
    ADD COLUMN IF NOT EXISTS cpf VARCHAR(20),
    ADD COLUMN IF NOT EXISTS telefone VARCHAR(20),
    ADD COLUMN IF NOT EXISTS cargo VARCHAR(100),
    ADD COLUMN IF NOT EXISTS data_nascimento DATE,
    ADD COLUMN IF NOT EXISTS departamento VARCHAR(100),
    ADD COLUMN IF NOT EXISTS foto_url VARCHAR(255);
