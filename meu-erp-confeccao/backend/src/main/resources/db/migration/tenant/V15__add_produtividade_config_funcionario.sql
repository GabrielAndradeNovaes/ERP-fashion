ALTER TABLE funcionarios ADD COLUMN meta_minima numeric(5,2) DEFAULT 75.00 NOT NULL;
ALTER TABLE funcionarios ADD COLUMN premio_100 numeric(10,2) DEFAULT 1000.00 NOT NULL;
ALTER TABLE funcionarios ADD COLUMN tempo_teorico integer DEFAULT 10000 NOT NULL;
