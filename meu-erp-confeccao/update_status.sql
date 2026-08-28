DO $$ 
DECLARE
    schema_record RECORD;
BEGIN
    FOR schema_record IN 
        SELECT schema_name 
        FROM information_schema.schemata 
        WHERE schema_name LIKE 'tenant_%' OR schema_name = 'master'
    LOOP
        EXECUTE format('UPDATE %I.ordens_producao SET status = ''PENDENTE'' WHERE status = ''CADASTRADA'';', schema_record.schema_name);
        EXECUTE format('UPDATE %I.ordens_producao SET status = ''EM_ANDAMENTO'' WHERE status IN (''CORTE'', ''COSTURA'', ''ESTAMPA'');', schema_record.schema_name);
    END LOOP;
END $$;
