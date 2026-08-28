DO $$ 
DECLARE 
    schema_name text; 
BEGIN 
    FOR schema_name IN SELECT nspname FROM pg_namespace WHERE nspname LIKE 'tenant_%' LOOP 
        EXECUTE 'ALTER TABLE ' || schema_name || '.ordens_producao DROP CONSTRAINT IF EXISTS ordens_producao_status_check;'; 
    END LOOP; 
END; 
$$;
