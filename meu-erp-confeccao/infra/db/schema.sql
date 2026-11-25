--
-- PostgreSQL database dump
--

\restrict 3OSl06fVJhviFLfSd0namp54kEC1m8C1vF9junuDv6y2BRskOmsNIYAKY9E0tbe

-- Dumped from database version 15.18
-- Dumped by pg_dump version 15.18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: master; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA master;


--
-- Name: tenant_0afd99ef; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA tenant_0afd99ef;


--
-- Name: tenant_1; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA tenant_1;


--
-- Name: tenant_c2b84f0e; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA tenant_c2b84f0e;


--
-- Name: tenant_df0f12fb; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA tenant_df0f12fb;


--
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: acessos_logs; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.acessos_logs (
    id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    usuario_id uuid NOT NULL,
    data_acesso timestamp without time zone NOT NULL,
    ip character varying(50),
    user_agent character varying(255)
);


--
-- Name: apontamentos; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.apontamentos (
    id uuid NOT NULL,
    data_hora timestamp(6) without time zone NOT NULL,
    cupom_id uuid NOT NULL,
    empresa_id uuid,
    funcionario_id uuid NOT NULL,
    data_pagamento timestamp(6) without time zone,
    pago boolean NOT NULL
);


--
-- Name: apontamentos_manuais; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.apontamentos_manuais (
    id uuid NOT NULL,
    data_hora timestamp(6) without time zone NOT NULL,
    data_pagamento timestamp(6) without time zone,
    minutos integer NOT NULL,
    observacao character varying(500),
    pago boolean NOT NULL,
    empresa_id uuid,
    funcionario_id uuid NOT NULL
);


--
-- Name: categorias; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.categorias (
    id uuid NOT NULL,
    deleted boolean NOT NULL,
    nome character varying(255) NOT NULL,
    tipo character varying(255),
    empresa_id uuid
);


--
-- Name: clientes; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.clientes (
    id uuid NOT NULL,
    deleted boolean NOT NULL,
    documento character varying(255),
    email character varying(255),
    nome character varying(255) NOT NULL,
    telefone character varying(255),
    empresa_id uuid,
    endereco jsonb,
    inscricao_estadual character varying(50),
    limite_credito numeric(10,2),
    razao_social character varying(255),
    status character varying(20),
    tabela_preco_padrao character varying(50),
    tipo_pessoa character varying(2)
);


--
-- Name: clientes_tenant; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.clientes_tenant (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome_empresa character varying(255) NOT NULL,
    schema_name character varying(50) NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    cnpj character varying(20),
    razao_social character varying(255),
    contato_nome character varying(255),
    contato_telefone character varying(20),
    status character varying(20) DEFAULT 'ATIVO'::character varying NOT NULL,
    nome_fantasia character varying(255),
    porte character varying(100),
    natureza_juridica character varying(200),
    status_rfb character varying(50),
    data_abertura date,
    email_principal character varying(255),
    telefone character varying(50),
    cep character varying(20),
    logradouro character varying(255),
    numero character varying(50),
    complemento character varying(255),
    bairro character varying(150),
    cidade character varying(150),
    estado character varying(2),
    cnae_principal_codigo character varying(20),
    cnae_principal_descricao character varying(255),
    simples_nacional boolean,
    receita_federal_raw_data jsonb,
    ativo boolean,
    slug character varying(100) NOT NULL
);


--
-- Name: cores; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.cores (
    id uuid NOT NULL,
    ativo boolean NOT NULL,
    codigo_hex character varying(20),
    criado_em timestamp(6) without time zone,
    nome character varying(100) NOT NULL,
    empresa_id uuid
);


--
-- Name: cupons; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.cupons (
    id uuid NOT NULL,
    codigo_barras character varying(100) NOT NULL,
    status character varying(20) NOT NULL,
    tempo_total_centesimal numeric(10,2) NOT NULL,
    empresa_id uuid,
    operacao_id uuid NOT NULL,
    pacote_id uuid NOT NULL,
    CONSTRAINT cupons_status_check CHECK (((status)::text = ANY ((ARRAY['PENDENTE'::character varying, 'PROCESSADO'::character varying])::text[])))
);


--
-- Name: empresas; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.empresas (
    id uuid NOT NULL,
    ativo boolean NOT NULL,
    cnpj character varying(255),
    nome_fantasia character varying(255) NOT NULL,
    razao_social character varying(255)
);


--
-- Name: estoque_movimentacoes; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.estoque_movimentacoes (
    id uuid NOT NULL,
    data_movimentacao timestamp(6) without time zone NOT NULL,
    documento_referencia character varying(100),
    quantidade numeric(19,4) NOT NULL,
    tipo character varying(20) NOT NULL,
    empresa_id uuid,
    material_id uuid NOT NULL,
    CONSTRAINT estoque_movimentacoes_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['ENTRADA'::character varying, 'SAIDA'::character varying])::text[])))
);


--
-- Name: estoque_produtos_movimentacoes; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.estoque_produtos_movimentacoes (
    id uuid NOT NULL,
    data_movimentacao timestamp(6) without time zone NOT NULL,
    documento_referencia character varying(100),
    quantidade integer NOT NULL,
    tipo character varying(20) NOT NULL,
    empresa_id uuid,
    produto_sku_id uuid NOT NULL,
    CONSTRAINT estoque_produtos_movimentacoes_tipo_check CHECK (((tipo)::text = ANY ((ARRAY['ENTRADA'::character varying, 'SAIDA'::character varying])::text[])))
);


--
-- Name: faturas_saas; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.faturas_saas (
    id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    descricao character varying(255) NOT NULL,
    valor numeric(19,4) NOT NULL,
    data_vencimento date NOT NULL,
    status character varying(50) NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    atualizado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: faturas_transacoes; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.faturas_transacoes (
    id uuid NOT NULL,
    fatura_saas_id uuid NOT NULL,
    gateway character varying(50) NOT NULL,
    gateway_transacao_id character varying(255),
    status character varying(50) NOT NULL,
    qr_code_payload text,
    qr_code_image_url text,
    payload_resposta text,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    atualizado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: fichas_tecnicas; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.fichas_tecnicas (
    id uuid NOT NULL,
    criado_em timestamp(6) without time zone,
    observacoes text,
    versao character varying(10) NOT NULL,
    empresa_id uuid,
    produto_base_id uuid NOT NULL
);


--
-- Name: fichas_tecnicas_materiais; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.fichas_tecnicas_materiais (
    id uuid NOT NULL,
    quantidade numeric(10,4) NOT NULL,
    empresa_id uuid,
    ficha_tecnica_id uuid NOT NULL,
    material_id uuid NOT NULL
);


--
-- Name: fichas_tecnicas_operacoes; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.fichas_tecnicas_operacoes (
    id uuid NOT NULL,
    faixa_comprimento character varying(30) NOT NULL,
    grau_dificuldade character varying(30) NOT NULL,
    maquina character varying(50),
    nome character varying(100) NOT NULL,
    ordem_execucao integer NOT NULL,
    quantidade_folhas integer NOT NULL,
    quantidade_paradas integer NOT NULL,
    tempo_calculado_centesimal numeric(10,2) NOT NULL,
    empresa_id uuid,
    ficha_tecnica_id uuid NOT NULL,
    comprimento_costura_cm numeric(10,2),
    dificuldade_tecido character varying(30),
    pontos_por_cm numeric(10,2),
    rpm_maquina integer,
    sam_minutos numeric(10,6),
    tipo_trajeto character varying(30),
    CONSTRAINT fichas_tecnicas_operacoes_dificuldade_tecido_check CHECK (((dificuldade_tecido)::text = ANY ((ARRAY['NORMAL'::character varying, 'MALHA'::character varying, 'ESCORREGADIO'::character varying, 'LISTRADO_XADREZ'::character varying])::text[]))),
    CONSTRAINT fichas_tecnicas_operacoes_faixa_comprimento_check CHECK (((faixa_comprimento)::text = ANY ((ARRAY['DE_0_A_60'::character varying, 'DE_61_A_90'::character varying, 'ACIMA_DE_91'::character varying])::text[]))),
    CONSTRAINT fichas_tecnicas_operacoes_grau_dificuldade_check CHECK (((grau_dificuldade)::text = ANY ((ARRAY['MUITO_FACIL'::character varying, 'FACIL'::character varying, 'MEDIO'::character varying, 'MEDIO_DIFICIL'::character varying, 'DIFICIL'::character varying])::text[]))),
    CONSTRAINT fichas_tecnicas_operacoes_tipo_trajeto_check CHECK (((tipo_trajeto)::text = ANY ((ARRAY['RETA'::character varying, 'CURVA'::character varying, 'CICLO_FIXO'::character varying])::text[])))
);


--
-- Name: flyway_schema_history; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


--
-- Name: fornecedores; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.fornecedores (
    id uuid NOT NULL,
    deleted boolean NOT NULL,
    documento character varying(255),
    email character varying(255),
    nome character varying(255) NOT NULL,
    telefone character varying(255),
    empresa_id uuid,
    categoria_fornecedor character varying(100),
    contato_nome character varying(255),
    endereco jsonb,
    inscricao_estadual character varying(50),
    prazo_pagamento_padrao integer,
    razao_social character varying(255),
    status character varying(20),
    tipo_pessoa character varying(2)
);


--
-- Name: funcionario_jornadas; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.funcionario_jornadas (
    id uuid NOT NULL,
    dia_semana integer NOT NULL,
    entrada time(6) without time zone NOT NULL,
    saida time(6) without time zone NOT NULL,
    funcionario_id uuid NOT NULL
);


--
-- Name: funcionarios; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.funcionarios (
    id uuid NOT NULL,
    ativo boolean NOT NULL,
    carga_horaria_diaria_padrao numeric(5,2) NOT NULL,
    carga_horaria_mensal_padrao numeric(5,2) NOT NULL,
    matricula character varying(50) NOT NULL,
    nome character varying(150) NOT NULL,
    empresa_id uuid,
    meta_minima numeric(5,2) NOT NULL,
    premio_100 numeric(10,2) NOT NULL,
    tempo_teorico integer NOT NULL,
    grupo_id uuid
);


--
-- Name: grupos_funcionarios; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.grupos_funcionarios (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    empresa_id uuid
);


--
-- Name: materiais; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.materiais (
    id uuid NOT NULL,
    codigo character varying(50) NOT NULL,
    criado_em timestamp(6) without time zone,
    custo_unitario numeric(10,2),
    descricao text,
    nome character varying(255) NOT NULL,
    quantidade_atual numeric(19,4),
    unidade_medida character varying(20) NOT NULL,
    empresa_id uuid,
    composicao character varying(255),
    fator_conversao numeric(10,4),
    fornecedor_padrao_id uuid,
    gramatura numeric(10,2),
    largura numeric(10,2),
    ncm character varying(20),
    rendimento numeric(10,2),
    status character varying(20),
    tipo_material character varying(50),
    unidade_compra character varying(20)
);


--
-- Name: ocorrencias; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.ocorrencias (
    id uuid NOT NULL,
    data_hora timestamp(6) without time zone NOT NULL,
    motivo character varying(255) NOT NULL,
    tempo_descontado_centesimal numeric(5,2) NOT NULL,
    empresa_id uuid,
    funcionario_id uuid NOT NULL
);


--
-- Name: ordens_producao; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.ordens_producao (
    id uuid NOT NULL,
    criado_em timestamp(6) without time zone NOT NULL,
    data_inicio timestamp(6) without time zone,
    numero character varying(50) NOT NULL,
    quantidade integer NOT NULL,
    status character varying(20) NOT NULL,
    empresa_id uuid,
    ficha_tecnica_id uuid NOT NULL,
    produto_base_id uuid NOT NULL,
    quantidade_produzida integer
);


--
-- Name: ordens_producao_itens; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.ordens_producao_itens (
    id uuid NOT NULL,
    quantidade integer NOT NULL,
    empresa_id uuid,
    ordem_producao_id uuid NOT NULL,
    produto_sku_id uuid NOT NULL
);


--
-- Name: pacotes; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.pacotes (
    id uuid NOT NULL,
    quantidade_pecas integer NOT NULL,
    sequencial integer NOT NULL,
    empresa_id uuid,
    ordem_producao_id uuid NOT NULL,
    produto_sku_id uuid NOT NULL,
    codigo_barras character varying(50),
    status character varying(20),
    CONSTRAINT pacotes_status_check CHECK (((status)::text = ANY ((ARRAY['PENDENTE'::character varying, 'PRODUZIDO'::character varying])::text[])))
);


--
-- Name: produtos_base; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.produtos_base (
    id uuid NOT NULL,
    codigo character varying(50) NOT NULL,
    criado_em timestamp(6) without time zone,
    descricao text,
    nome character varying(255) NOT NULL,
    preco_custo numeric(10,2),
    preco_venda numeric(10,2),
    empresa_id uuid,
    categoria character varying(100),
    cest character varying(20),
    colecao character varying(100),
    genero character varying(20),
    marca character varying(100),
    ncm character varying(20),
    origem character varying(50),
    peso_bruto numeric(10,3),
    peso_liquido numeric(10,3),
    status character varying(20)
);


--
-- Name: produtos_skus; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.produtos_skus (
    id uuid NOT NULL,
    codigo_barras character varying(100),
    cor character varying(50) NOT NULL,
    criado_em timestamp(6) without time zone,
    preco_venda numeric(10,2),
    quantidade_atual integer NOT NULL,
    tamanho character varying(20) NOT NULL,
    empresa_id uuid,
    produto_base_id uuid NOT NULL
);


--
-- Name: tabela_tempo_padrao; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.tabela_tempo_padrao (
    id uuid NOT NULL,
    faixa_comprimento character varying(30) NOT NULL,
    grau_dificuldade character varying(30) NOT NULL,
    indice integer NOT NULL,
    tempo_centesimal numeric(10,2) NOT NULL,
    empresa_id uuid,
    CONSTRAINT tabela_tempo_padrao_faixa_comprimento_check CHECK (((faixa_comprimento)::text = ANY ((ARRAY['DE_0_A_60'::character varying, 'DE_61_A_90'::character varying, 'ACIMA_DE_91'::character varying])::text[]))),
    CONSTRAINT tabela_tempo_padrao_grau_dificuldade_check CHECK (((grau_dificuldade)::text = ANY ((ARRAY['MUITO_FACIL'::character varying, 'FACIL'::character varying, 'MEDIO'::character varying, 'MEDIO_DIFICIL'::character varying, 'DIFICIL'::character varying])::text[])))
);


--
-- Name: tamanhos; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.tamanhos (
    id uuid NOT NULL,
    ativo boolean NOT NULL,
    criado_em timestamp(6) without time zone,
    nome character varying(100) NOT NULL,
    sigla character varying(20),
    empresa_id uuid
);


--
-- Name: tenant_contracts; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.tenant_contracts (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    tenant_id character varying(50) NOT NULL,
    valor_mensal numeric(10,2) NOT NULL,
    data_inicio date NOT NULL,
    data_cancelamento date,
    status_contrato character varying(20) DEFAULT 'ATIVO'::character varying NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: tenant_invoices; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.tenant_invoices (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    contract_id uuid NOT NULL,
    data_vencimento date NOT NULL,
    data_pagamento date,
    valor numeric(10,2) NOT NULL,
    status_fatura character varying(20) DEFAULT 'PENDENTE'::character varying NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: tenant_modules; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.tenant_modules (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    tenant_id character varying(50) NOT NULL,
    module_name character varying(50) NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: titulos_pagar; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.titulos_pagar (
    id uuid NOT NULL,
    data_emissao date NOT NULL,
    data_pagamento date,
    data_vencimento date NOT NULL,
    descricao character varying(200) NOT NULL,
    status character varying(20) NOT NULL,
    valor numeric(12,2) NOT NULL,
    empresa_id uuid NOT NULL,
    funcionario_id uuid,
    CONSTRAINT titulos_pagar_status_check CHECK (((status)::text = ANY ((ARRAY['PENDENTE'::character varying, 'PAGO'::character varying, 'CANCELADO'::character varying])::text[])))
);


--
-- Name: unidades_medida; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.unidades_medida (
    id uuid NOT NULL,
    deleted boolean NOT NULL,
    nome character varying(255) NOT NULL,
    sigla character varying(255) NOT NULL,
    empresa_id uuid
);


--
-- Name: usuario_empresas; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.usuario_empresas (
    empresa_id uuid NOT NULL,
    usuario_id uuid NOT NULL
);


--
-- Name: usuario_permissoes; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.usuario_permissoes (
    usuario_id uuid NOT NULL,
    permissao character varying(100) NOT NULL
);


--
-- Name: usuarios; Type: TABLE; Schema: master; Owner: -
--

CREATE TABLE master.usuarios (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    tenant_id character varying(50) NOT NULL,
    nome character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    senha character varying(255) NOT NULL,
    role character varying(50) DEFAULT 'USER'::character varying,
    ativo boolean DEFAULT true,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    filial_principal_id uuid,
    cargo character varying(100),
    cpf character varying(20),
    data_nascimento date,
    departamento character varying(100),
    foto_url character varying(255),
    telefone character varying(20)
);


--
-- Name: apontamentos; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.apontamentos (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    cupom_id uuid NOT NULL,
    funcionario_id uuid NOT NULL,
    data_hora timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    pago boolean DEFAULT false NOT NULL,
    data_pagamento timestamp without time zone
);


--
-- Name: apontamentos_manuais; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.apontamentos_manuais (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    empresa_id uuid,
    funcionario_id uuid NOT NULL,
    data_hora timestamp without time zone NOT NULL,
    minutos integer NOT NULL,
    observacao character varying(500),
    pago boolean DEFAULT false NOT NULL,
    data_pagamento timestamp without time zone
);


--
-- Name: categorias; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.categorias (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(255) NOT NULL,
    tipo character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: clientes; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.clientes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(255) NOT NULL,
    documento character varying(255),
    email character varying(255),
    telefone character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tipo_pessoa character varying(2) DEFAULT 'PJ'::character varying,
    razao_social character varying(255),
    inscricao_estadual character varying(50),
    limite_credito numeric(10,2),
    tabela_preco_padrao character varying(50),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    endereco jsonb
);


--
-- Name: cores; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.cores (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    codigo_hex character varying(20),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid
);


--
-- Name: cupons; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.cupons (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    pacote_id uuid NOT NULL,
    operacao_id uuid NOT NULL,
    codigo_barras character varying(100) NOT NULL,
    tempo_total_centesimal numeric(10,2) NOT NULL,
    status character varying(20) DEFAULT 'PENDENTE'::character varying NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: departamentos; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.departamentos (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    empresa_id uuid
);


--
-- Name: empresas; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.empresas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome_fantasia character varying(255) NOT NULL,
    razao_social character varying(255),
    cnpj character varying(20),
    ativo boolean DEFAULT true NOT NULL
);


--
-- Name: estoque_movimentacoes; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.estoque_movimentacoes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    material_id uuid NOT NULL,
    tipo character varying(20) NOT NULL,
    quantidade numeric(19,4) NOT NULL,
    documento_referencia character varying(100),
    data_movimentacao timestamp without time zone NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: estoque_produtos_movimentacoes; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.estoque_produtos_movimentacoes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    produto_sku_id uuid NOT NULL,
    tipo character varying(20) NOT NULL,
    quantidade integer NOT NULL,
    documento_referencia character varying(100),
    data_movimentacao timestamp without time zone NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: fichas_tecnicas; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.fichas_tecnicas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    produto_base_id uuid NOT NULL,
    versao character varying(10) NOT NULL,
    observacoes text,
    criado_em timestamp without time zone,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    ativa boolean DEFAULT true
);


--
-- Name: fichas_tecnicas_materiais; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.fichas_tecnicas_materiais (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ficha_tecnica_id uuid NOT NULL,
    material_id uuid NOT NULL,
    quantidade numeric(10,4) NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: fichas_tecnicas_operacoes; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.fichas_tecnicas_operacoes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ficha_tecnica_id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    maquina character varying(50),
    ordem_execucao integer NOT NULL,
    quantidade_folhas integer NOT NULL,
    quantidade_paradas integer NOT NULL,
    tempo_calculado_centesimal numeric(10,2) NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    rpm_maquina integer,
    pontos_por_cm numeric(10,2),
    comprimento_costura_cm numeric(10,2),
    tipo_trajeto character varying(30),
    dificuldade_tecido character varying(30),
    sam_minutos numeric(10,6)
);


--
-- Name: flyway_schema_history; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


--
-- Name: fornecedores; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.fornecedores (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(255) NOT NULL,
    documento character varying(255),
    email character varying(255),
    telefone character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tipo_pessoa character varying(2) DEFAULT 'PJ'::character varying,
    razao_social character varying(255),
    inscricao_estadual character varying(50),
    categoria_fornecedor character varying(100),
    prazo_pagamento_padrao integer,
    contato_nome character varying(255),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    endereco jsonb,
    avaliacao integer DEFAULT 5
);


--
-- Name: funcionario_jornadas; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.funcionario_jornadas (
    id uuid NOT NULL,
    funcionario_id uuid NOT NULL,
    dia_semana integer NOT NULL,
    entrada time without time zone NOT NULL,
    saida time without time zone NOT NULL
);


--
-- Name: funcionarios; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.funcionarios (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(150) NOT NULL,
    matricula character varying(50) NOT NULL,
    carga_horaria_diaria_padrao numeric(5,2) NOT NULL,
    carga_horaria_mensal_padrao numeric(5,2) NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    meta_minima numeric(5,2) DEFAULT 75.00 NOT NULL,
    premio_100 numeric(10,2) DEFAULT 1000.00 NOT NULL,
    grupo_id uuid
);


--
-- Name: grupos_funcionarios; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.grupos_funcionarios (
    id uuid NOT NULL,
    empresa_id uuid,
    nome character varying(100) NOT NULL
);


--
-- Name: localizacoes; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.localizacoes (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    tipo character varying(50),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    empresa_id uuid
);


--
-- Name: materiais; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.materiais (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    codigo character varying(50) NOT NULL,
    nome character varying(255) NOT NULL,
    descricao text,
    unidade_medida_legacy character varying(20),
    custo_unitario numeric(10,2),
    quantidade_atual numeric(19,4) DEFAULT 0,
    criado_em timestamp without time zone,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tipo_material character varying(50),
    composicao character varying(255),
    ncm character varying(20),
    unidade_compra character varying(20),
    fator_conversao numeric(10,4),
    largura numeric(10,2),
    gramatura numeric(10,2),
    rendimento numeric(10,2),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    fornecedor_padrao_id uuid,
    unidade_medida_id uuid
);


--
-- Name: materiais_fornecedor; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.materiais_fornecedor (
    id uuid NOT NULL,
    empresa_id uuid NOT NULL,
    fornecedor_id uuid NOT NULL,
    material_id uuid NOT NULL,
    codigo_fornecedor character varying(100) NOT NULL,
    descricao_fornecedor character varying(255),
    fator_conversao numeric(10,4) DEFAULT 1.0
);


--
-- Name: ocorrencias; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.ocorrencias (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    funcionario_id uuid NOT NULL,
    motivo character varying(255) NOT NULL,
    tempo_descontado_centesimal numeric(5,2) NOT NULL,
    data_hora timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: ordens_compra; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.ordens_compra (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    empresa_id uuid NOT NULL,
    fornecedor_id uuid NOT NULL,
    numero_pedido character varying(50) NOT NULL,
    data_emissao timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    data_previsao_entrega date,
    status character varying(50) DEFAULT 'RASCUNHO'::character varying NOT NULL,
    valor_total numeric(12,2) DEFAULT 0,
    observacoes text,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    atualizado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: ordens_compra_itens; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.ordens_compra_itens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem_compra_id uuid NOT NULL,
    material_id uuid NOT NULL,
    quantidade_solicitada numeric(12,3) NOT NULL,
    quantidade_recebida numeric(12,3) DEFAULT 0,
    preco_unitario numeric(10,2) NOT NULL,
    valor_total numeric(12,2) NOT NULL
);


--
-- Name: ordens_producao; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.ordens_producao (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    numero character varying(50) NOT NULL,
    produto_base_id uuid NOT NULL,
    ficha_tecnica_id uuid NOT NULL,
    quantidade integer NOT NULL,
    status character varying(20) NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    data_inicio timestamp without time zone,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    quantidade_produzida integer DEFAULT 0
);


--
-- Name: ordens_producao_itens; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.ordens_producao_itens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem_producao_id uuid NOT NULL,
    produto_sku_id uuid NOT NULL,
    quantidade integer NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: pacotes; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.pacotes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem_producao_id uuid NOT NULL,
    produto_sku_id uuid NOT NULL,
    sequencial integer NOT NULL,
    quantidade_pecas integer NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    status character varying(20) DEFAULT 'PENDENTE'::character varying NOT NULL,
    codigo_barras character varying(50)
);


--
-- Name: produtos_base; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.produtos_base (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    codigo character varying(50) NOT NULL,
    nome character varying(255) NOT NULL,
    descricao text,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    preco_venda numeric(10,2),
    preco_custo numeric(10,2),
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    marca character varying(100),
    categoria_legacy character varying(100),
    colecao character varying(100),
    genero character varying(20),
    ncm character varying(20),
    cest character varying(20),
    origem character varying(50),
    peso_bruto numeric(10,3),
    peso_liquido numeric(10,3),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    categoria_id uuid
);


--
-- Name: produtos_skus; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.produtos_skus (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    produto_base_id uuid NOT NULL,
    cor_legacy character varying(50),
    tamanho_legacy character varying(20),
    codigo_barras character varying(100),
    preco_venda numeric(10,2),
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    quantidade_atual integer DEFAULT 0 NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    cor_id uuid,
    tamanho_id uuid
);


--
-- Name: recebimentos_nf; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.recebimentos_nf (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    empresa_id uuid NOT NULL,
    fornecedor_id uuid,
    ordem_compra_id uuid,
    chave_acesso_nfe character varying(100),
    numero_nfe character varying(50),
    data_emissao timestamp without time zone,
    data_recebimento timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    xml_conteudo text,
    valor_total_nf numeric(12,2) DEFAULT 0,
    status character varying(50) DEFAULT 'PENDENTE'::character varying NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: recebimentos_nf_itens; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.recebimentos_nf_itens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    recebimento_nf_id uuid NOT NULL,
    material_id uuid,
    nome_produto_nf character varying(255),
    codigo_produto_nf character varying(100),
    ncm_nf character varying(20),
    quantidade numeric(12,3) NOT NULL,
    valor_unitario numeric(10,2) NOT NULL,
    valor_total numeric(12,2) NOT NULL
);


--
-- Name: tabela_tempo_padrao; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.tabela_tempo_padrao (
    id uuid NOT NULL,
    indice numeric(10,2) NOT NULL,
    grau_dificuldade character varying(50) NOT NULL,
    faixa_comprimento character varying(50) NOT NULL,
    tempo_centesimal numeric(10,4) NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: tamanhos; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.tamanhos (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    sigla character varying(20),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid
);


--
-- Name: titulos_pagar; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.titulos_pagar (
    id uuid NOT NULL,
    data_emissao date NOT NULL,
    data_pagamento date,
    data_vencimento date NOT NULL,
    descricao character varying(200) NOT NULL,
    status character varying(20) NOT NULL,
    valor numeric(12,2) NOT NULL,
    empresa_id uuid NOT NULL,
    funcionario_id uuid,
    CONSTRAINT titulos_pagar_status_check CHECK (((status)::text = ANY ((ARRAY['PENDENTE'::character varying, 'PAGO'::character varying, 'CANCELADO'::character varying])::text[])))
);


--
-- Name: titulos_receber; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.titulos_receber (
    id uuid NOT NULL,
    data_emissao date NOT NULL,
    data_pagamento date,
    data_vencimento date NOT NULL,
    descricao character varying(200) NOT NULL,
    status character varying(20) NOT NULL,
    valor numeric(12,2) NOT NULL,
    empresa_id uuid NOT NULL,
    cliente_id uuid,
    CONSTRAINT titulos_receber_status_check CHECK (((status)::text = ANY ((ARRAY['PENDENTE'::character varying, 'RECEBIDO'::character varying, 'CANCELADO'::character varying])::text[])))
);


--
-- Name: unidades_medida; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.unidades_medida (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    sigla character varying(255) NOT NULL,
    nome character varying(255) NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    ativo boolean DEFAULT true NOT NULL
);


--
-- Name: usuario_empresas; Type: TABLE; Schema: tenant_0afd99ef; Owner: -
--

CREATE TABLE tenant_0afd99ef.usuario_empresas (
    usuario_id uuid NOT NULL,
    empresa_id uuid NOT NULL
);


--
-- Name: apontamentos; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.apontamentos (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    cupom_id uuid NOT NULL,
    funcionario_id uuid NOT NULL,
    data_hora timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    pago boolean DEFAULT false NOT NULL,
    data_pagamento timestamp without time zone
);


--
-- Name: apontamentos_manuais; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.apontamentos_manuais (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    empresa_id uuid,
    funcionario_id uuid NOT NULL,
    data_hora timestamp without time zone NOT NULL,
    minutos integer NOT NULL,
    observacao character varying(500),
    pago boolean DEFAULT false NOT NULL,
    data_pagamento timestamp without time zone
);


--
-- Name: categorias; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.categorias (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(255) NOT NULL,
    tipo character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: clientes; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.clientes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(255) NOT NULL,
    documento character varying(255),
    email character varying(255),
    telefone character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tipo_pessoa character varying(2) DEFAULT 'PJ'::character varying,
    razao_social character varying(255),
    inscricao_estadual character varying(50),
    limite_credito numeric(10,2),
    tabela_preco_padrao character varying(50),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    endereco jsonb
);


--
-- Name: cores; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.cores (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    codigo_hex character varying(20),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid
);


--
-- Name: cupons; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.cupons (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    pacote_id uuid NOT NULL,
    operacao_id uuid NOT NULL,
    codigo_barras character varying(100) NOT NULL,
    tempo_total_centesimal numeric(10,2) NOT NULL,
    status character varying(20) DEFAULT 'PENDENTE'::character varying NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: departamentos; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.departamentos (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    empresa_id uuid
);


--
-- Name: empresas; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.empresas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome_fantasia character varying(255) NOT NULL,
    razao_social character varying(255),
    cnpj character varying(20),
    ativo boolean DEFAULT true NOT NULL
);


--
-- Name: estoque_movimentacoes; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.estoque_movimentacoes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    material_id uuid NOT NULL,
    tipo character varying(20) NOT NULL,
    quantidade numeric(19,4) NOT NULL,
    documento_referencia character varying(100),
    data_movimentacao timestamp without time zone NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: estoque_produtos_movimentacoes; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.estoque_produtos_movimentacoes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    produto_sku_id uuid NOT NULL,
    tipo character varying(20) NOT NULL,
    quantidade integer NOT NULL,
    documento_referencia character varying(100),
    data_movimentacao timestamp without time zone NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: fichas_tecnicas; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.fichas_tecnicas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    produto_base_id uuid NOT NULL,
    versao character varying(10) NOT NULL,
    observacoes text,
    criado_em timestamp without time zone,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    ativa boolean DEFAULT true
);


--
-- Name: fichas_tecnicas_materiais; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.fichas_tecnicas_materiais (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ficha_tecnica_id uuid NOT NULL,
    material_id uuid NOT NULL,
    quantidade numeric(10,4) NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: fichas_tecnicas_operacoes; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.fichas_tecnicas_operacoes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ficha_tecnica_id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    maquina character varying(50),
    ordem_execucao integer NOT NULL,
    quantidade_folhas integer NOT NULL,
    quantidade_paradas integer NOT NULL,
    tempo_calculado_centesimal numeric(10,2) NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    rpm_maquina integer,
    pontos_por_cm numeric(10,2),
    comprimento_costura_cm numeric(10,2),
    tipo_trajeto character varying(30),
    dificuldade_tecido character varying(30),
    sam_minutos numeric(10,6)
);


--
-- Name: flyway_schema_history; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


--
-- Name: fornecedores; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.fornecedores (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(255) NOT NULL,
    documento character varying(255),
    email character varying(255),
    telefone character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tipo_pessoa character varying(2) DEFAULT 'PJ'::character varying,
    razao_social character varying(255),
    inscricao_estadual character varying(50),
    categoria_fornecedor character varying(100),
    prazo_pagamento_padrao integer,
    contato_nome character varying(255),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    endereco jsonb,
    avaliacao integer DEFAULT 5
);


--
-- Name: funcionario_jornadas; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.funcionario_jornadas (
    id uuid NOT NULL,
    funcionario_id uuid NOT NULL,
    dia_semana integer NOT NULL,
    entrada time without time zone NOT NULL,
    saida time without time zone NOT NULL
);


--
-- Name: funcionarios; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.funcionarios (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(150) NOT NULL,
    matricula character varying(50) NOT NULL,
    carga_horaria_diaria_padrao numeric(5,2) NOT NULL,
    carga_horaria_mensal_padrao numeric(5,2) NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    meta_minima numeric(5,2) DEFAULT 75.00 NOT NULL,
    premio_100 numeric(10,2) DEFAULT 1000.00 NOT NULL,
    grupo_id uuid
);


--
-- Name: grupos_funcionarios; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.grupos_funcionarios (
    id uuid NOT NULL,
    empresa_id uuid,
    nome character varying(100) NOT NULL
);


--
-- Name: localizacoes; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.localizacoes (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    tipo character varying(50),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    empresa_id uuid
);


--
-- Name: materiais; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.materiais (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    codigo character varying(50) NOT NULL,
    nome character varying(255) NOT NULL,
    descricao text,
    unidade_medida_legacy character varying(20),
    custo_unitario numeric(10,2),
    quantidade_atual numeric(19,4) DEFAULT 0,
    criado_em timestamp without time zone,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tipo_material character varying(50),
    composicao character varying(255),
    ncm character varying(20),
    unidade_compra character varying(20),
    fator_conversao numeric(10,4),
    largura numeric(10,2),
    gramatura numeric(10,2),
    rendimento numeric(10,2),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    fornecedor_padrao_id uuid,
    unidade_medida_id uuid
);


--
-- Name: materiais_fornecedor; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.materiais_fornecedor (
    id uuid NOT NULL,
    empresa_id uuid NOT NULL,
    fornecedor_id uuid NOT NULL,
    material_id uuid NOT NULL,
    codigo_fornecedor character varying(100) NOT NULL,
    descricao_fornecedor character varying(255),
    fator_conversao numeric(10,4) DEFAULT 1.0
);


--
-- Name: ocorrencias; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.ocorrencias (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    funcionario_id uuid NOT NULL,
    motivo character varying(255) NOT NULL,
    tempo_descontado_centesimal numeric(5,2) NOT NULL,
    data_hora timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: ordens_compra; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.ordens_compra (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    empresa_id uuid NOT NULL,
    fornecedor_id uuid NOT NULL,
    numero_pedido character varying(50) NOT NULL,
    data_emissao timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    data_previsao_entrega date,
    status character varying(50) DEFAULT 'RASCUNHO'::character varying NOT NULL,
    valor_total numeric(12,2) DEFAULT 0,
    observacoes text,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    atualizado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: ordens_compra_itens; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.ordens_compra_itens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem_compra_id uuid NOT NULL,
    material_id uuid NOT NULL,
    quantidade_solicitada numeric(12,3) NOT NULL,
    quantidade_recebida numeric(12,3) DEFAULT 0,
    preco_unitario numeric(10,2) NOT NULL,
    valor_total numeric(12,2) NOT NULL
);


--
-- Name: ordens_producao; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.ordens_producao (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    numero character varying(50) NOT NULL,
    produto_base_id uuid NOT NULL,
    ficha_tecnica_id uuid NOT NULL,
    quantidade integer NOT NULL,
    status character varying(20) NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    data_inicio timestamp without time zone,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    quantidade_produzida integer DEFAULT 0
);


--
-- Name: ordens_producao_itens; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.ordens_producao_itens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem_producao_id uuid NOT NULL,
    produto_sku_id uuid NOT NULL,
    quantidade integer NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: pacotes; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.pacotes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem_producao_id uuid NOT NULL,
    produto_sku_id uuid NOT NULL,
    sequencial integer NOT NULL,
    quantidade_pecas integer NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    status character varying(20) DEFAULT 'PENDENTE'::character varying NOT NULL,
    codigo_barras character varying(50)
);


--
-- Name: produtos_base; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.produtos_base (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    codigo character varying(50) NOT NULL,
    nome character varying(255) NOT NULL,
    descricao text,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    preco_venda numeric(10,2),
    preco_custo numeric(10,2),
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    marca character varying(100),
    categoria_legacy character varying(100),
    colecao character varying(100),
    genero character varying(20),
    ncm character varying(20),
    cest character varying(20),
    origem character varying(50),
    peso_bruto numeric(10,3),
    peso_liquido numeric(10,3),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    categoria_id uuid
);


--
-- Name: produtos_skus; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.produtos_skus (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    produto_base_id uuid NOT NULL,
    cor_legacy character varying(50),
    tamanho_legacy character varying(20),
    codigo_barras character varying(100),
    preco_venda numeric(10,2),
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    quantidade_atual integer DEFAULT 0 NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    cor_id uuid,
    tamanho_id uuid
);


--
-- Name: recebimentos_nf; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.recebimentos_nf (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    empresa_id uuid NOT NULL,
    fornecedor_id uuid,
    ordem_compra_id uuid,
    chave_acesso_nfe character varying(100),
    numero_nfe character varying(50),
    data_emissao timestamp without time zone,
    data_recebimento timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    xml_conteudo text,
    valor_total_nf numeric(12,2) DEFAULT 0,
    status character varying(50) DEFAULT 'PENDENTE'::character varying NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: recebimentos_nf_itens; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.recebimentos_nf_itens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    recebimento_nf_id uuid NOT NULL,
    material_id uuid,
    nome_produto_nf character varying(255),
    codigo_produto_nf character varying(100),
    ncm_nf character varying(20),
    quantidade numeric(12,3) NOT NULL,
    valor_unitario numeric(10,2) NOT NULL,
    valor_total numeric(12,2) NOT NULL
);


--
-- Name: tabela_tempo_padrao; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.tabela_tempo_padrao (
    id uuid NOT NULL,
    indice numeric(10,2) NOT NULL,
    grau_dificuldade character varying(50) NOT NULL,
    faixa_comprimento character varying(50) NOT NULL,
    tempo_centesimal numeric(10,4) NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: tamanhos; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.tamanhos (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    sigla character varying(20),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid
);


--
-- Name: titulos_pagar; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.titulos_pagar (
    id uuid NOT NULL,
    data_emissao date NOT NULL,
    data_pagamento date,
    data_vencimento date NOT NULL,
    descricao character varying(200) NOT NULL,
    status character varying(20) NOT NULL,
    valor numeric(12,2) NOT NULL,
    empresa_id uuid NOT NULL,
    funcionario_id uuid,
    CONSTRAINT titulos_pagar_status_check CHECK (((status)::text = ANY ((ARRAY['PENDENTE'::character varying, 'PAGO'::character varying, 'CANCELADO'::character varying])::text[])))
);


--
-- Name: titulos_receber; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.titulos_receber (
    id uuid NOT NULL,
    data_emissao date NOT NULL,
    data_pagamento date,
    data_vencimento date NOT NULL,
    descricao character varying(200) NOT NULL,
    status character varying(20) NOT NULL,
    valor numeric(12,2) NOT NULL,
    empresa_id uuid NOT NULL,
    cliente_id uuid,
    CONSTRAINT titulos_receber_status_check CHECK (((status)::text = ANY ((ARRAY['PENDENTE'::character varying, 'RECEBIDO'::character varying, 'CANCELADO'::character varying])::text[])))
);


--
-- Name: unidades_medida; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.unidades_medida (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    sigla character varying(255) NOT NULL,
    nome character varying(255) NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    ativo boolean DEFAULT true NOT NULL
);


--
-- Name: usuario_empresas; Type: TABLE; Schema: tenant_1; Owner: -
--

CREATE TABLE tenant_1.usuario_empresas (
    usuario_id uuid NOT NULL,
    empresa_id uuid NOT NULL
);


--
-- Name: apontamentos; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.apontamentos (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    cupom_id uuid NOT NULL,
    funcionario_id uuid NOT NULL,
    data_hora timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    pago boolean DEFAULT false NOT NULL,
    data_pagamento timestamp without time zone
);


--
-- Name: apontamentos_manuais; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.apontamentos_manuais (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    empresa_id uuid,
    funcionario_id uuid NOT NULL,
    data_hora timestamp without time zone NOT NULL,
    minutos integer NOT NULL,
    observacao character varying(500),
    pago boolean DEFAULT false NOT NULL,
    data_pagamento timestamp without time zone
);


--
-- Name: categorias; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.categorias (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(255) NOT NULL,
    tipo character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: clientes; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.clientes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(255) NOT NULL,
    documento character varying(255),
    email character varying(255),
    telefone character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tipo_pessoa character varying(2) DEFAULT 'PJ'::character varying,
    razao_social character varying(255),
    inscricao_estadual character varying(50),
    limite_credito numeric(10,2),
    tabela_preco_padrao character varying(50),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    endereco jsonb
);


--
-- Name: cores; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.cores (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    codigo_hex character varying(20),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid
);


--
-- Name: cupons; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.cupons (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    pacote_id uuid NOT NULL,
    operacao_id uuid NOT NULL,
    codigo_barras character varying(100) NOT NULL,
    tempo_total_centesimal numeric(10,2) NOT NULL,
    status character varying(20) DEFAULT 'PENDENTE'::character varying NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: departamentos; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.departamentos (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    empresa_id uuid
);


--
-- Name: empresas; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.empresas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome_fantasia character varying(255) NOT NULL,
    razao_social character varying(255),
    cnpj character varying(20),
    ativo boolean DEFAULT true NOT NULL
);


--
-- Name: estoque_movimentacoes; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.estoque_movimentacoes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    material_id uuid NOT NULL,
    tipo character varying(20) NOT NULL,
    quantidade numeric(19,4) NOT NULL,
    documento_referencia character varying(100),
    data_movimentacao timestamp without time zone NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: estoque_produtos_movimentacoes; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.estoque_produtos_movimentacoes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    produto_sku_id uuid NOT NULL,
    tipo character varying(20) NOT NULL,
    quantidade integer NOT NULL,
    documento_referencia character varying(100),
    data_movimentacao timestamp without time zone NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: fichas_tecnicas; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.fichas_tecnicas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    produto_base_id uuid NOT NULL,
    versao character varying(10) NOT NULL,
    observacoes text,
    criado_em timestamp without time zone,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    ativa boolean DEFAULT true
);


--
-- Name: fichas_tecnicas_materiais; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.fichas_tecnicas_materiais (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ficha_tecnica_id uuid NOT NULL,
    material_id uuid NOT NULL,
    quantidade numeric(10,4) NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: fichas_tecnicas_operacoes; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.fichas_tecnicas_operacoes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ficha_tecnica_id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    maquina character varying(50),
    ordem_execucao integer NOT NULL,
    quantidade_folhas integer NOT NULL,
    quantidade_paradas integer NOT NULL,
    tempo_calculado_centesimal numeric(10,2) NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    rpm_maquina integer,
    pontos_por_cm numeric(10,2),
    comprimento_costura_cm numeric(10,2),
    tipo_trajeto character varying(30),
    dificuldade_tecido character varying(30),
    sam_minutos numeric(10,6)
);


--
-- Name: flyway_schema_history; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


--
-- Name: fornecedores; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.fornecedores (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(255) NOT NULL,
    documento character varying(255),
    email character varying(255),
    telefone character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tipo_pessoa character varying(2) DEFAULT 'PJ'::character varying,
    razao_social character varying(255),
    inscricao_estadual character varying(50),
    categoria_fornecedor character varying(100),
    prazo_pagamento_padrao integer,
    contato_nome character varying(255),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    endereco jsonb,
    avaliacao integer DEFAULT 5
);


--
-- Name: funcionario_jornadas; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.funcionario_jornadas (
    id uuid NOT NULL,
    funcionario_id uuid NOT NULL,
    dia_semana integer NOT NULL,
    entrada time without time zone NOT NULL,
    saida time without time zone NOT NULL
);


--
-- Name: funcionarios; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.funcionarios (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(150) NOT NULL,
    matricula character varying(50) NOT NULL,
    carga_horaria_diaria_padrao numeric(5,2) NOT NULL,
    carga_horaria_mensal_padrao numeric(5,2) NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    meta_minima numeric(5,2) DEFAULT 75.00 NOT NULL,
    premio_100 numeric(10,2) DEFAULT 1000.00 NOT NULL,
    grupo_id uuid
);


--
-- Name: grupos_funcionarios; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.grupos_funcionarios (
    id uuid NOT NULL,
    empresa_id uuid,
    nome character varying(100) NOT NULL
);


--
-- Name: localizacoes; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.localizacoes (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    tipo character varying(50),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    empresa_id uuid
);


--
-- Name: materiais; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.materiais (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    codigo character varying(50) NOT NULL,
    nome character varying(255) NOT NULL,
    descricao text,
    unidade_medida_legacy character varying(20),
    custo_unitario numeric(10,2),
    quantidade_atual numeric(19,4) DEFAULT 0,
    criado_em timestamp without time zone,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tipo_material character varying(50),
    composicao character varying(255),
    ncm character varying(20),
    unidade_compra character varying(20),
    fator_conversao numeric(10,4),
    largura numeric(10,2),
    gramatura numeric(10,2),
    rendimento numeric(10,2),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    fornecedor_padrao_id uuid,
    unidade_medida_id uuid
);


--
-- Name: materiais_fornecedor; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.materiais_fornecedor (
    id uuid NOT NULL,
    empresa_id uuid NOT NULL,
    fornecedor_id uuid NOT NULL,
    material_id uuid NOT NULL,
    codigo_fornecedor character varying(100) NOT NULL,
    descricao_fornecedor character varying(255),
    fator_conversao numeric(10,4) DEFAULT 1.0
);


--
-- Name: ocorrencias; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.ocorrencias (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    funcionario_id uuid NOT NULL,
    motivo character varying(255) NOT NULL,
    tempo_descontado_centesimal numeric(5,2) NOT NULL,
    data_hora timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: ordens_compra; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.ordens_compra (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    empresa_id uuid NOT NULL,
    fornecedor_id uuid NOT NULL,
    numero_pedido character varying(50) NOT NULL,
    data_emissao timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    data_previsao_entrega date,
    status character varying(50) DEFAULT 'RASCUNHO'::character varying NOT NULL,
    valor_total numeric(12,2) DEFAULT 0,
    observacoes text,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    atualizado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: ordens_compra_itens; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.ordens_compra_itens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem_compra_id uuid NOT NULL,
    material_id uuid NOT NULL,
    quantidade_solicitada numeric(12,3) NOT NULL,
    quantidade_recebida numeric(12,3) DEFAULT 0,
    preco_unitario numeric(10,2) NOT NULL,
    valor_total numeric(12,2) NOT NULL
);


--
-- Name: ordens_producao; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.ordens_producao (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    numero character varying(50) NOT NULL,
    produto_base_id uuid NOT NULL,
    ficha_tecnica_id uuid NOT NULL,
    quantidade integer NOT NULL,
    status character varying(20) NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    data_inicio timestamp without time zone,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    quantidade_produzida integer DEFAULT 0
);


--
-- Name: ordens_producao_itens; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.ordens_producao_itens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem_producao_id uuid NOT NULL,
    produto_sku_id uuid NOT NULL,
    quantidade integer NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: pacotes; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.pacotes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem_producao_id uuid NOT NULL,
    produto_sku_id uuid NOT NULL,
    sequencial integer NOT NULL,
    quantidade_pecas integer NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    status character varying(20) DEFAULT 'PENDENTE'::character varying NOT NULL,
    codigo_barras character varying(50)
);


--
-- Name: produtos_base; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.produtos_base (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    codigo character varying(50) NOT NULL,
    nome character varying(255) NOT NULL,
    descricao text,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    preco_venda numeric(10,2),
    preco_custo numeric(10,2),
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    marca character varying(100),
    categoria_legacy character varying(100),
    colecao character varying(100),
    genero character varying(20),
    ncm character varying(20),
    cest character varying(20),
    origem character varying(50),
    peso_bruto numeric(10,3),
    peso_liquido numeric(10,3),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    categoria_id uuid
);


--
-- Name: produtos_skus; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.produtos_skus (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    produto_base_id uuid NOT NULL,
    cor_legacy character varying(50),
    tamanho_legacy character varying(20),
    codigo_barras character varying(100),
    preco_venda numeric(10,2),
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    quantidade_atual integer DEFAULT 0 NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    cor_id uuid,
    tamanho_id uuid
);


--
-- Name: recebimentos_nf; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.recebimentos_nf (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    empresa_id uuid NOT NULL,
    fornecedor_id uuid,
    ordem_compra_id uuid,
    chave_acesso_nfe character varying(100),
    numero_nfe character varying(50),
    data_emissao timestamp without time zone,
    data_recebimento timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    xml_conteudo text,
    valor_total_nf numeric(12,2) DEFAULT 0,
    status character varying(50) DEFAULT 'PENDENTE'::character varying NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: recebimentos_nf_itens; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.recebimentos_nf_itens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    recebimento_nf_id uuid NOT NULL,
    material_id uuid,
    nome_produto_nf character varying(255),
    codigo_produto_nf character varying(100),
    ncm_nf character varying(20),
    quantidade numeric(12,3) NOT NULL,
    valor_unitario numeric(10,2) NOT NULL,
    valor_total numeric(12,2) NOT NULL
);


--
-- Name: tabela_tempo_padrao; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.tabela_tempo_padrao (
    id uuid NOT NULL,
    indice numeric(10,2) NOT NULL,
    grau_dificuldade character varying(50) NOT NULL,
    faixa_comprimento character varying(50) NOT NULL,
    tempo_centesimal numeric(10,4) NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: tamanhos; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.tamanhos (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    sigla character varying(20),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid
);


--
-- Name: titulos_pagar; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.titulos_pagar (
    id uuid NOT NULL,
    data_emissao date NOT NULL,
    data_pagamento date,
    data_vencimento date NOT NULL,
    descricao character varying(200) NOT NULL,
    status character varying(20) NOT NULL,
    valor numeric(12,2) NOT NULL,
    empresa_id uuid NOT NULL,
    funcionario_id uuid,
    CONSTRAINT titulos_pagar_status_check CHECK (((status)::text = ANY ((ARRAY['PENDENTE'::character varying, 'PAGO'::character varying, 'CANCELADO'::character varying])::text[])))
);


--
-- Name: titulos_receber; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.titulos_receber (
    id uuid NOT NULL,
    data_emissao date NOT NULL,
    data_pagamento date,
    data_vencimento date NOT NULL,
    descricao character varying(200) NOT NULL,
    status character varying(20) NOT NULL,
    valor numeric(12,2) NOT NULL,
    empresa_id uuid NOT NULL,
    cliente_id uuid,
    CONSTRAINT titulos_receber_status_check CHECK (((status)::text = ANY ((ARRAY['PENDENTE'::character varying, 'RECEBIDO'::character varying, 'CANCELADO'::character varying])::text[])))
);


--
-- Name: unidades_medida; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.unidades_medida (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    sigla character varying(255) NOT NULL,
    nome character varying(255) NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    ativo boolean DEFAULT true NOT NULL
);


--
-- Name: usuario_empresas; Type: TABLE; Schema: tenant_c2b84f0e; Owner: -
--

CREATE TABLE tenant_c2b84f0e.usuario_empresas (
    usuario_id uuid NOT NULL,
    empresa_id uuid NOT NULL
);


--
-- Name: apontamentos; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.apontamentos (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    cupom_id uuid NOT NULL,
    funcionario_id uuid NOT NULL,
    data_hora timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    pago boolean DEFAULT false NOT NULL,
    data_pagamento timestamp without time zone
);


--
-- Name: apontamentos_manuais; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.apontamentos_manuais (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    empresa_id uuid,
    funcionario_id uuid NOT NULL,
    data_hora timestamp without time zone NOT NULL,
    minutos integer NOT NULL,
    observacao character varying(500),
    pago boolean DEFAULT false NOT NULL,
    data_pagamento timestamp without time zone
);


--
-- Name: categorias; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.categorias (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(255) NOT NULL,
    tipo character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: clientes; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.clientes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(255) NOT NULL,
    documento character varying(255),
    email character varying(255),
    telefone character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tipo_pessoa character varying(2) DEFAULT 'PJ'::character varying,
    razao_social character varying(255),
    inscricao_estadual character varying(50),
    limite_credito numeric(10,2),
    tabela_preco_padrao character varying(50),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    endereco jsonb
);


--
-- Name: cores; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.cores (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    codigo_hex character varying(20),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid
);


--
-- Name: cupons; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.cupons (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    pacote_id uuid NOT NULL,
    operacao_id uuid NOT NULL,
    codigo_barras character varying(100) NOT NULL,
    tempo_total_centesimal numeric(10,2) NOT NULL,
    status character varying(20) DEFAULT 'PENDENTE'::character varying NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: departamentos; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.departamentos (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    empresa_id uuid
);


--
-- Name: empresas; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.empresas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome_fantasia character varying(255) NOT NULL,
    razao_social character varying(255),
    cnpj character varying(20),
    ativo boolean DEFAULT true NOT NULL
);


--
-- Name: estoque_movimentacoes; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.estoque_movimentacoes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    material_id uuid NOT NULL,
    tipo character varying(20) NOT NULL,
    quantidade numeric(19,4) NOT NULL,
    documento_referencia character varying(100),
    data_movimentacao timestamp without time zone NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: estoque_produtos_movimentacoes; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.estoque_produtos_movimentacoes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    produto_sku_id uuid NOT NULL,
    tipo character varying(20) NOT NULL,
    quantidade integer NOT NULL,
    documento_referencia character varying(100),
    data_movimentacao timestamp without time zone NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: fichas_tecnicas; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.fichas_tecnicas (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    produto_base_id uuid NOT NULL,
    versao character varying(10) NOT NULL,
    observacoes text,
    criado_em timestamp without time zone,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    ativa boolean DEFAULT true
);


--
-- Name: fichas_tecnicas_materiais; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.fichas_tecnicas_materiais (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ficha_tecnica_id uuid NOT NULL,
    material_id uuid NOT NULL,
    quantidade numeric(10,4) NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: fichas_tecnicas_operacoes; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.fichas_tecnicas_operacoes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ficha_tecnica_id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    maquina character varying(50),
    ordem_execucao integer NOT NULL,
    quantidade_folhas integer NOT NULL,
    quantidade_paradas integer NOT NULL,
    tempo_calculado_centesimal numeric(10,2) NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    rpm_maquina integer,
    pontos_por_cm numeric(10,2),
    comprimento_costura_cm numeric(10,2),
    tipo_trajeto character varying(30),
    dificuldade_tecido character varying(30),
    sam_minutos numeric(10,6)
);


--
-- Name: flyway_schema_history; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


--
-- Name: fornecedores; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.fornecedores (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(255) NOT NULL,
    documento character varying(255),
    email character varying(255),
    telefone character varying(255),
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tipo_pessoa character varying(2) DEFAULT 'PJ'::character varying,
    razao_social character varying(255),
    inscricao_estadual character varying(50),
    categoria_fornecedor character varying(100),
    prazo_pagamento_padrao integer,
    contato_nome character varying(255),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    endereco jsonb,
    avaliacao integer DEFAULT 5
);


--
-- Name: funcionario_jornadas; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.funcionario_jornadas (
    id uuid NOT NULL,
    funcionario_id uuid NOT NULL,
    dia_semana integer NOT NULL,
    entrada time without time zone NOT NULL,
    saida time without time zone NOT NULL
);


--
-- Name: funcionarios; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.funcionarios (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    nome character varying(150) NOT NULL,
    matricula character varying(50) NOT NULL,
    carga_horaria_diaria_padrao numeric(5,2) NOT NULL,
    carga_horaria_mensal_padrao numeric(5,2) NOT NULL,
    ativo boolean DEFAULT true NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    meta_minima numeric(5,2) DEFAULT 75.00 NOT NULL,
    premio_100 numeric(10,2) DEFAULT 1000.00 NOT NULL,
    grupo_id uuid
);


--
-- Name: grupos_funcionarios; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.grupos_funcionarios (
    id uuid NOT NULL,
    empresa_id uuid,
    nome character varying(100) NOT NULL
);


--
-- Name: localizacoes; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.localizacoes (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    tipo character varying(50),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    empresa_id uuid
);


--
-- Name: materiais; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.materiais (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    codigo character varying(50) NOT NULL,
    nome character varying(255) NOT NULL,
    descricao text,
    unidade_medida_legacy character varying(20),
    custo_unitario numeric(10,2),
    quantidade_atual numeric(19,4) DEFAULT 0,
    criado_em timestamp without time zone,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    tipo_material character varying(50),
    composicao character varying(255),
    ncm character varying(20),
    unidade_compra character varying(20),
    fator_conversao numeric(10,4),
    largura numeric(10,2),
    gramatura numeric(10,2),
    rendimento numeric(10,2),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    fornecedor_padrao_id uuid,
    unidade_medida_id uuid
);


--
-- Name: materiais_fornecedor; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.materiais_fornecedor (
    id uuid NOT NULL,
    empresa_id uuid NOT NULL,
    fornecedor_id uuid NOT NULL,
    material_id uuid NOT NULL,
    codigo_fornecedor character varying(100) NOT NULL,
    descricao_fornecedor character varying(255),
    fator_conversao numeric(10,4) DEFAULT 1.0
);


--
-- Name: ocorrencias; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.ocorrencias (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    funcionario_id uuid NOT NULL,
    motivo character varying(255) NOT NULL,
    tempo_descontado_centesimal numeric(5,2) NOT NULL,
    data_hora timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: ordens_compra; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.ordens_compra (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    empresa_id uuid NOT NULL,
    fornecedor_id uuid NOT NULL,
    numero_pedido character varying(50) NOT NULL,
    data_emissao timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    data_previsao_entrega date,
    status character varying(50) DEFAULT 'RASCUNHO'::character varying NOT NULL,
    valor_total numeric(12,2) DEFAULT 0,
    observacoes text,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    atualizado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: ordens_compra_itens; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.ordens_compra_itens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem_compra_id uuid NOT NULL,
    material_id uuid NOT NULL,
    quantidade_solicitada numeric(12,3) NOT NULL,
    quantidade_recebida numeric(12,3) DEFAULT 0,
    preco_unitario numeric(10,2) NOT NULL,
    valor_total numeric(12,2) NOT NULL
);


--
-- Name: ordens_producao; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.ordens_producao (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    numero character varying(50) NOT NULL,
    produto_base_id uuid NOT NULL,
    ficha_tecnica_id uuid NOT NULL,
    quantidade integer NOT NULL,
    status character varying(20) NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    data_inicio timestamp without time zone,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    quantidade_produzida integer DEFAULT 0
);


--
-- Name: ordens_producao_itens; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.ordens_producao_itens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem_producao_id uuid NOT NULL,
    produto_sku_id uuid NOT NULL,
    quantidade integer NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: pacotes; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.pacotes (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    ordem_producao_id uuid NOT NULL,
    produto_sku_id uuid NOT NULL,
    sequencial integer NOT NULL,
    quantidade_pecas integer NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    status character varying(20) DEFAULT 'PENDENTE'::character varying NOT NULL
);


--
-- Name: produtos_base; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.produtos_base (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    codigo character varying(50) NOT NULL,
    nome character varying(255) NOT NULL,
    descricao text,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    preco_venda numeric(10,2),
    preco_custo numeric(10,2),
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    marca character varying(100),
    categoria_legacy character varying(100),
    colecao character varying(100),
    genero character varying(20),
    ncm character varying(20),
    cest character varying(20),
    origem character varying(50),
    peso_bruto numeric(10,3),
    peso_liquido numeric(10,3),
    status character varying(20) DEFAULT 'ATIVO'::character varying,
    categoria_id uuid
);


--
-- Name: produtos_skus; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.produtos_skus (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    produto_base_id uuid NOT NULL,
    cor_legacy character varying(50),
    tamanho_legacy character varying(20),
    codigo_barras character varying(100),
    preco_venda numeric(10,2),
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    quantidade_atual integer DEFAULT 0 NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    cor_id uuid,
    tamanho_id uuid
);


--
-- Name: recebimentos_nf; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.recebimentos_nf (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    empresa_id uuid NOT NULL,
    fornecedor_id uuid,
    ordem_compra_id uuid,
    chave_acesso_nfe character varying(100),
    numero_nfe character varying(50),
    data_emissao timestamp without time zone,
    data_recebimento timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    xml_conteudo text,
    valor_total_nf numeric(12,2) DEFAULT 0,
    status character varying(50) DEFAULT 'PENDENTE'::character varying NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: recebimentos_nf_itens; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.recebimentos_nf_itens (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    recebimento_nf_id uuid NOT NULL,
    material_id uuid,
    nome_produto_nf character varying(255),
    codigo_produto_nf character varying(100),
    ncm_nf character varying(20),
    quantidade numeric(12,3) NOT NULL,
    valor_unitario numeric(10,2) NOT NULL,
    valor_total numeric(12,2) NOT NULL
);


--
-- Name: tabela_tempo_padrao; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.tabela_tempo_padrao (
    id uuid NOT NULL,
    indice numeric(10,2) NOT NULL,
    grau_dificuldade character varying(50) NOT NULL,
    faixa_comprimento character varying(50) NOT NULL,
    tempo_centesimal numeric(10,4) NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid
);


--
-- Name: tamanhos; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.tamanhos (
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    sigla character varying(20),
    ativo boolean DEFAULT true NOT NULL,
    criado_em timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    empresa_id uuid
);


--
-- Name: titulos_pagar; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.titulos_pagar (
    id uuid NOT NULL,
    data_emissao date NOT NULL,
    data_pagamento date,
    data_vencimento date NOT NULL,
    descricao character varying(200) NOT NULL,
    status character varying(20) NOT NULL,
    valor numeric(12,2) NOT NULL,
    empresa_id uuid NOT NULL,
    funcionario_id uuid,
    CONSTRAINT titulos_pagar_status_check CHECK (((status)::text = ANY ((ARRAY['PENDENTE'::character varying, 'PAGO'::character varying, 'CANCELADO'::character varying])::text[])))
);


--
-- Name: titulos_receber; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.titulos_receber (
    id uuid NOT NULL,
    data_emissao date NOT NULL,
    data_pagamento date,
    data_vencimento date NOT NULL,
    descricao character varying(200) NOT NULL,
    status character varying(20) NOT NULL,
    valor numeric(12,2) NOT NULL,
    empresa_id uuid NOT NULL,
    cliente_id uuid,
    CONSTRAINT titulos_receber_status_check CHECK (((status)::text = ANY ((ARRAY['PENDENTE'::character varying, 'RECEBIDO'::character varying, 'CANCELADO'::character varying])::text[])))
);


--
-- Name: unidades_medida; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.unidades_medida (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    sigla character varying(255) NOT NULL,
    nome character varying(255) NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    empresa_id uuid DEFAULT '00000000-0000-0000-0000-000000000000'::uuid,
    ativo boolean DEFAULT true NOT NULL
);


--
-- Name: usuario_empresas; Type: TABLE; Schema: tenant_df0f12fb; Owner: -
--

CREATE TABLE tenant_df0f12fb.usuario_empresas (
    usuario_id uuid NOT NULL,
    empresa_id uuid NOT NULL
);


--
-- Name: acessos_logs acessos_logs_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.acessos_logs
    ADD CONSTRAINT acessos_logs_pkey PRIMARY KEY (id);


--
-- Name: apontamentos_manuais apontamentos_manuais_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_pkey PRIMARY KEY (id);


--
-- Name: apontamentos apontamentos_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.apontamentos
    ADD CONSTRAINT apontamentos_pkey PRIMARY KEY (id);


--
-- Name: categorias categorias_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.categorias
    ADD CONSTRAINT categorias_pkey PRIMARY KEY (id);


--
-- Name: clientes clientes_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.clientes
    ADD CONSTRAINT clientes_pkey PRIMARY KEY (id);


--
-- Name: clientes_tenant clientes_tenant_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.clientes_tenant
    ADD CONSTRAINT clientes_tenant_pkey PRIMARY KEY (id);


--
-- Name: clientes_tenant clientes_tenant_schema_name_key; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.clientes_tenant
    ADD CONSTRAINT clientes_tenant_schema_name_key UNIQUE (schema_name);


--
-- Name: cores cores_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.cores
    ADD CONSTRAINT cores_pkey PRIMARY KEY (id);


--
-- Name: cupons cupons_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.cupons
    ADD CONSTRAINT cupons_pkey PRIMARY KEY (id);


--
-- Name: empresas empresas_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.empresas
    ADD CONSTRAINT empresas_pkey PRIMARY KEY (id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_pkey PRIMARY KEY (id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_pkey PRIMARY KEY (id);


--
-- Name: faturas_saas faturas_saas_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.faturas_saas
    ADD CONSTRAINT faturas_saas_pkey PRIMARY KEY (id);


--
-- Name: faturas_transacoes faturas_transacoes_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.faturas_transacoes
    ADD CONSTRAINT faturas_transacoes_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas fichas_tecnicas_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: fornecedores fornecedores_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fornecedores
    ADD CONSTRAINT fornecedores_pkey PRIMARY KEY (id);


--
-- Name: funcionario_jornadas funcionario_jornadas_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.funcionario_jornadas
    ADD CONSTRAINT funcionario_jornadas_pkey PRIMARY KEY (id);


--
-- Name: funcionarios funcionarios_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.funcionarios
    ADD CONSTRAINT funcionarios_pkey PRIMARY KEY (id);


--
-- Name: grupos_funcionarios grupos_funcionarios_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.grupos_funcionarios
    ADD CONSTRAINT grupos_funcionarios_pkey PRIMARY KEY (id);


--
-- Name: materiais materiais_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.materiais
    ADD CONSTRAINT materiais_pkey PRIMARY KEY (id);


--
-- Name: ocorrencias ocorrencias_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.ocorrencias
    ADD CONSTRAINT ocorrencias_pkey PRIMARY KEY (id);


--
-- Name: ordens_producao_itens ordens_producao_itens_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_pkey PRIMARY KEY (id);


--
-- Name: ordens_producao ordens_producao_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.ordens_producao
    ADD CONSTRAINT ordens_producao_pkey PRIMARY KEY (id);


--
-- Name: pacotes pacotes_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.pacotes
    ADD CONSTRAINT pacotes_pkey PRIMARY KEY (id);


--
-- Name: produtos_base produtos_base_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.produtos_base
    ADD CONSTRAINT produtos_base_pkey PRIMARY KEY (id);


--
-- Name: produtos_skus produtos_skus_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.produtos_skus
    ADD CONSTRAINT produtos_skus_pkey PRIMARY KEY (id);


--
-- Name: tabela_tempo_padrao tabela_tempo_padrao_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.tabela_tempo_padrao
    ADD CONSTRAINT tabela_tempo_padrao_pkey PRIMARY KEY (id);


--
-- Name: tamanhos tamanhos_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.tamanhos
    ADD CONSTRAINT tamanhos_pkey PRIMARY KEY (id);


--
-- Name: tenant_contracts tenant_contracts_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.tenant_contracts
    ADD CONSTRAINT tenant_contracts_pkey PRIMARY KEY (id);


--
-- Name: tenant_invoices tenant_invoices_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.tenant_invoices
    ADD CONSTRAINT tenant_invoices_pkey PRIMARY KEY (id);


--
-- Name: tenant_modules tenant_modules_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.tenant_modules
    ADD CONSTRAINT tenant_modules_pkey PRIMARY KEY (id);


--
-- Name: titulos_pagar titulos_pagar_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.titulos_pagar
    ADD CONSTRAINT titulos_pagar_pkey PRIMARY KEY (id);


--
-- Name: tabela_tempo_padrao uk8jyma9vrm9k5rp10xr2uf8ql6; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.tabela_tempo_padrao
    ADD CONSTRAINT uk8jyma9vrm9k5rp10xr2uf8ql6 UNIQUE (indice, grau_dificuldade, faixa_comprimento);


--
-- Name: funcionarios uk_4lbc0sp4tfy69tqf60v426tv6; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.funcionarios
    ADD CONSTRAINT uk_4lbc0sp4tfy69tqf60v426tv6 UNIQUE (matricula);


--
-- Name: produtos_skus uk_4sp9arl0coaknkejp8fcn0tdi; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.produtos_skus
    ADD CONSTRAINT uk_4sp9arl0coaknkejp8fcn0tdi UNIQUE (codigo_barras);


--
-- Name: fichas_tecnicas uk_92vcdxlj64yv0kqwlipeaviyk; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fichas_tecnicas
    ADD CONSTRAINT uk_92vcdxlj64yv0kqwlipeaviyk UNIQUE (produto_base_id);


--
-- Name: pacotes uk_94dy5rtj3d5o35g9bouumitoo; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.pacotes
    ADD CONSTRAINT uk_94dy5rtj3d5o35g9bouumitoo UNIQUE (codigo_barras);


--
-- Name: produtos_base uk_cjynuca35c6dxs2u5il9vewxa; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.produtos_base
    ADD CONSTRAINT uk_cjynuca35c6dxs2u5il9vewxa UNIQUE (codigo);


--
-- Name: clientes_tenant uk_clientes_tenant_slug; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.clientes_tenant
    ADD CONSTRAINT uk_clientes_tenant_slug UNIQUE (slug);


--
-- Name: ordens_producao uk_lcyh1eb12bxl9fhgqs6y2x9bh; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.ordens_producao
    ADD CONSTRAINT uk_lcyh1eb12bxl9fhgqs6y2x9bh UNIQUE (numero);


--
-- Name: cupons uk_mpuswfflhq657mk633kmwooab; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.cupons
    ADD CONSTRAINT uk_mpuswfflhq657mk633kmwooab UNIQUE (codigo_barras);


--
-- Name: materiais uk_mxcghe0ulktr6f97os6uvspnk; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.materiais
    ADD CONSTRAINT uk_mxcghe0ulktr6f97os6uvspnk UNIQUE (codigo);


--
-- Name: apontamentos uk_rx3wo6g190nk3mn23qoq7htw4; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.apontamentos
    ADD CONSTRAINT uk_rx3wo6g190nk3mn23qoq7htw4 UNIQUE (cupom_id);


--
-- Name: tenant_modules uk_tenant_module; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.tenant_modules
    ADD CONSTRAINT uk_tenant_module UNIQUE (tenant_id, module_name);


--
-- Name: unidades_medida unidades_medida_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.unidades_medida
    ADD CONSTRAINT unidades_medida_pkey PRIMARY KEY (id);


--
-- Name: usuario_empresas usuario_empresas_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.usuario_empresas
    ADD CONSTRAINT usuario_empresas_pkey PRIMARY KEY (empresa_id, usuario_id);


--
-- Name: usuario_permissoes usuario_permissoes_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.usuario_permissoes
    ADD CONSTRAINT usuario_permissoes_pkey PRIMARY KEY (usuario_id, permissao);


--
-- Name: usuarios usuarios_email_key; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.usuarios
    ADD CONSTRAINT usuarios_email_key UNIQUE (email);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- Name: apontamentos apontamentos_cupom_id_key; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.apontamentos
    ADD CONSTRAINT apontamentos_cupom_id_key UNIQUE (cupom_id);


--
-- Name: apontamentos_manuais apontamentos_manuais_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_pkey PRIMARY KEY (id);


--
-- Name: apontamentos apontamentos_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.apontamentos
    ADD CONSTRAINT apontamentos_pkey PRIMARY KEY (id);


--
-- Name: categorias categorias_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.categorias
    ADD CONSTRAINT categorias_pkey PRIMARY KEY (id);


--
-- Name: clientes clientes_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.clientes
    ADD CONSTRAINT clientes_pkey PRIMARY KEY (id);


--
-- Name: cores cores_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.cores
    ADD CONSTRAINT cores_pkey PRIMARY KEY (id);


--
-- Name: cupons cupons_codigo_barras_key; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.cupons
    ADD CONSTRAINT cupons_codigo_barras_key UNIQUE (codigo_barras);


--
-- Name: cupons cupons_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.cupons
    ADD CONSTRAINT cupons_pkey PRIMARY KEY (id);


--
-- Name: departamentos departamentos_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.departamentos
    ADD CONSTRAINT departamentos_pkey PRIMARY KEY (id);


--
-- Name: empresas empresas_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.empresas
    ADD CONSTRAINT empresas_pkey PRIMARY KEY (id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_pkey PRIMARY KEY (id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas fichas_tecnicas_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: fornecedores fornecedores_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.fornecedores
    ADD CONSTRAINT fornecedores_pkey PRIMARY KEY (id);


--
-- Name: funcionario_jornadas funcionario_jornadas_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.funcionario_jornadas
    ADD CONSTRAINT funcionario_jornadas_pkey PRIMARY KEY (id);


--
-- Name: funcionarios funcionarios_matricula_key; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.funcionarios
    ADD CONSTRAINT funcionarios_matricula_key UNIQUE (matricula);


--
-- Name: funcionarios funcionarios_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.funcionarios
    ADD CONSTRAINT funcionarios_pkey PRIMARY KEY (id);


--
-- Name: grupos_funcionarios grupos_funcionarios_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.grupos_funcionarios
    ADD CONSTRAINT grupos_funcionarios_pkey PRIMARY KEY (id);


--
-- Name: localizacoes localizacoes_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.localizacoes
    ADD CONSTRAINT localizacoes_pkey PRIMARY KEY (id);


--
-- Name: materiais materiais_codigo_key; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.materiais
    ADD CONSTRAINT materiais_codigo_key UNIQUE (codigo);


--
-- Name: materiais_fornecedor materiais_fornecedor_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.materiais_fornecedor
    ADD CONSTRAINT materiais_fornecedor_pkey PRIMARY KEY (id);


--
-- Name: materiais materiais_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.materiais
    ADD CONSTRAINT materiais_pkey PRIMARY KEY (id);


--
-- Name: ocorrencias ocorrencias_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ocorrencias
    ADD CONSTRAINT ocorrencias_pkey PRIMARY KEY (id);


--
-- Name: ordens_compra_itens ordens_compra_itens_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_compra_itens
    ADD CONSTRAINT ordens_compra_itens_pkey PRIMARY KEY (id);


--
-- Name: ordens_compra ordens_compra_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_compra
    ADD CONSTRAINT ordens_compra_pkey PRIMARY KEY (id);


--
-- Name: ordens_producao_itens ordens_producao_itens_ordem_producao_id_produto_sku_id_key; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_ordem_producao_id_produto_sku_id_key UNIQUE (ordem_producao_id, produto_sku_id);


--
-- Name: ordens_producao_itens ordens_producao_itens_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_pkey PRIMARY KEY (id);


--
-- Name: ordens_producao ordens_producao_numero_key; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_producao
    ADD CONSTRAINT ordens_producao_numero_key UNIQUE (numero);


--
-- Name: ordens_producao ordens_producao_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_producao
    ADD CONSTRAINT ordens_producao_pkey PRIMARY KEY (id);


--
-- Name: pacotes pacotes_ordem_producao_id_produto_sku_id_sequencial_key; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.pacotes
    ADD CONSTRAINT pacotes_ordem_producao_id_produto_sku_id_sequencial_key UNIQUE (ordem_producao_id, produto_sku_id, sequencial);


--
-- Name: pacotes pacotes_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.pacotes
    ADD CONSTRAINT pacotes_pkey PRIMARY KEY (id);


--
-- Name: produtos_base produtos_base_codigo_key; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.produtos_base
    ADD CONSTRAINT produtos_base_codigo_key UNIQUE (codigo);


--
-- Name: produtos_base produtos_base_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.produtos_base
    ADD CONSTRAINT produtos_base_pkey PRIMARY KEY (id);


--
-- Name: produtos_skus produtos_skus_codigo_barras_key; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.produtos_skus
    ADD CONSTRAINT produtos_skus_codigo_barras_key UNIQUE (codigo_barras);


--
-- Name: produtos_skus produtos_skus_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.produtos_skus
    ADD CONSTRAINT produtos_skus_pkey PRIMARY KEY (id);


--
-- Name: produtos_skus produtos_skus_produto_base_id_cor_tamanho_key; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.produtos_skus
    ADD CONSTRAINT produtos_skus_produto_base_id_cor_tamanho_key UNIQUE (produto_base_id, cor_legacy, tamanho_legacy);


--
-- Name: recebimentos_nf_itens recebimentos_nf_itens_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.recebimentos_nf_itens
    ADD CONSTRAINT recebimentos_nf_itens_pkey PRIMARY KEY (id);


--
-- Name: recebimentos_nf recebimentos_nf_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.recebimentos_nf
    ADD CONSTRAINT recebimentos_nf_pkey PRIMARY KEY (id);


--
-- Name: tabela_tempo_padrao tabela_tempo_padrao_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.tabela_tempo_padrao
    ADD CONSTRAINT tabela_tempo_padrao_pkey PRIMARY KEY (id);


--
-- Name: tamanhos tamanhos_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.tamanhos
    ADD CONSTRAINT tamanhos_pkey PRIMARY KEY (id);


--
-- Name: titulos_pagar titulos_pagar_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.titulos_pagar
    ADD CONSTRAINT titulos_pagar_pkey PRIMARY KEY (id);


--
-- Name: titulos_receber titulos_receber_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.titulos_receber
    ADD CONSTRAINT titulos_receber_pkey PRIMARY KEY (id);


--
-- Name: materiais_fornecedor uk_fornecedor_codigo; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.materiais_fornecedor
    ADD CONSTRAINT uk_fornecedor_codigo UNIQUE (fornecedor_id, codigo_fornecedor);


--
-- Name: grupos_funcionarios uk_grupo_nome; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.grupos_funcionarios
    ADD CONSTRAINT uk_grupo_nome UNIQUE (empresa_id, nome);


--
-- Name: tabela_tempo_padrao uk_tabela_tempo_padrao; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.tabela_tempo_padrao
    ADD CONSTRAINT uk_tabela_tempo_padrao UNIQUE (indice, grau_dificuldade, faixa_comprimento);


--
-- Name: unidades_medida unidades_medida_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.unidades_medida
    ADD CONSTRAINT unidades_medida_pkey PRIMARY KEY (id);


--
-- Name: pacotes uq_tenant_0afd99ef_pacotes_codigo_barras; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.pacotes
    ADD CONSTRAINT uq_tenant_0afd99ef_pacotes_codigo_barras UNIQUE (codigo_barras);


--
-- Name: usuario_empresas usuario_empresas_pkey; Type: CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.usuario_empresas
    ADD CONSTRAINT usuario_empresas_pkey PRIMARY KEY (usuario_id, empresa_id);


--
-- Name: apontamentos apontamentos_cupom_id_key; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.apontamentos
    ADD CONSTRAINT apontamentos_cupom_id_key UNIQUE (cupom_id);


--
-- Name: apontamentos_manuais apontamentos_manuais_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_pkey PRIMARY KEY (id);


--
-- Name: apontamentos apontamentos_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.apontamentos
    ADD CONSTRAINT apontamentos_pkey PRIMARY KEY (id);


--
-- Name: categorias categorias_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.categorias
    ADD CONSTRAINT categorias_pkey PRIMARY KEY (id);


--
-- Name: clientes clientes_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.clientes
    ADD CONSTRAINT clientes_pkey PRIMARY KEY (id);


--
-- Name: cores cores_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.cores
    ADD CONSTRAINT cores_pkey PRIMARY KEY (id);


--
-- Name: cupons cupons_codigo_barras_key; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.cupons
    ADD CONSTRAINT cupons_codigo_barras_key UNIQUE (codigo_barras);


--
-- Name: cupons cupons_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.cupons
    ADD CONSTRAINT cupons_pkey PRIMARY KEY (id);


--
-- Name: departamentos departamentos_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.departamentos
    ADD CONSTRAINT departamentos_pkey PRIMARY KEY (id);


--
-- Name: empresas empresas_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.empresas
    ADD CONSTRAINT empresas_pkey PRIMARY KEY (id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_pkey PRIMARY KEY (id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas fichas_tecnicas_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: fornecedores fornecedores_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.fornecedores
    ADD CONSTRAINT fornecedores_pkey PRIMARY KEY (id);


--
-- Name: funcionario_jornadas funcionario_jornadas_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.funcionario_jornadas
    ADD CONSTRAINT funcionario_jornadas_pkey PRIMARY KEY (id);


--
-- Name: funcionarios funcionarios_matricula_key; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.funcionarios
    ADD CONSTRAINT funcionarios_matricula_key UNIQUE (matricula);


--
-- Name: funcionarios funcionarios_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.funcionarios
    ADD CONSTRAINT funcionarios_pkey PRIMARY KEY (id);


--
-- Name: grupos_funcionarios grupos_funcionarios_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.grupos_funcionarios
    ADD CONSTRAINT grupos_funcionarios_pkey PRIMARY KEY (id);


--
-- Name: localizacoes localizacoes_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.localizacoes
    ADD CONSTRAINT localizacoes_pkey PRIMARY KEY (id);


--
-- Name: materiais materiais_codigo_key; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.materiais
    ADD CONSTRAINT materiais_codigo_key UNIQUE (codigo);


--
-- Name: materiais_fornecedor materiais_fornecedor_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.materiais_fornecedor
    ADD CONSTRAINT materiais_fornecedor_pkey PRIMARY KEY (id);


--
-- Name: materiais materiais_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.materiais
    ADD CONSTRAINT materiais_pkey PRIMARY KEY (id);


--
-- Name: ocorrencias ocorrencias_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ocorrencias
    ADD CONSTRAINT ocorrencias_pkey PRIMARY KEY (id);


--
-- Name: ordens_compra_itens ordens_compra_itens_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_compra_itens
    ADD CONSTRAINT ordens_compra_itens_pkey PRIMARY KEY (id);


--
-- Name: ordens_compra ordens_compra_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_compra
    ADD CONSTRAINT ordens_compra_pkey PRIMARY KEY (id);


--
-- Name: ordens_producao_itens ordens_producao_itens_ordem_producao_id_produto_sku_id_key; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_ordem_producao_id_produto_sku_id_key UNIQUE (ordem_producao_id, produto_sku_id);


--
-- Name: ordens_producao_itens ordens_producao_itens_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_pkey PRIMARY KEY (id);


--
-- Name: ordens_producao ordens_producao_numero_key; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_producao
    ADD CONSTRAINT ordens_producao_numero_key UNIQUE (numero);


--
-- Name: ordens_producao ordens_producao_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_producao
    ADD CONSTRAINT ordens_producao_pkey PRIMARY KEY (id);


--
-- Name: pacotes pacotes_ordem_producao_id_produto_sku_id_sequencial_key; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.pacotes
    ADD CONSTRAINT pacotes_ordem_producao_id_produto_sku_id_sequencial_key UNIQUE (ordem_producao_id, produto_sku_id, sequencial);


--
-- Name: pacotes pacotes_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.pacotes
    ADD CONSTRAINT pacotes_pkey PRIMARY KEY (id);


--
-- Name: produtos_base produtos_base_codigo_key; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.produtos_base
    ADD CONSTRAINT produtos_base_codigo_key UNIQUE (codigo);


--
-- Name: produtos_base produtos_base_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.produtos_base
    ADD CONSTRAINT produtos_base_pkey PRIMARY KEY (id);


--
-- Name: produtos_skus produtos_skus_codigo_barras_key; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.produtos_skus
    ADD CONSTRAINT produtos_skus_codigo_barras_key UNIQUE (codigo_barras);


--
-- Name: produtos_skus produtos_skus_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.produtos_skus
    ADD CONSTRAINT produtos_skus_pkey PRIMARY KEY (id);


--
-- Name: produtos_skus produtos_skus_produto_base_id_cor_tamanho_key; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.produtos_skus
    ADD CONSTRAINT produtos_skus_produto_base_id_cor_tamanho_key UNIQUE (produto_base_id, cor_legacy, tamanho_legacy);


--
-- Name: recebimentos_nf_itens recebimentos_nf_itens_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.recebimentos_nf_itens
    ADD CONSTRAINT recebimentos_nf_itens_pkey PRIMARY KEY (id);


--
-- Name: recebimentos_nf recebimentos_nf_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.recebimentos_nf
    ADD CONSTRAINT recebimentos_nf_pkey PRIMARY KEY (id);


--
-- Name: tabela_tempo_padrao tabela_tempo_padrao_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.tabela_tempo_padrao
    ADD CONSTRAINT tabela_tempo_padrao_pkey PRIMARY KEY (id);


--
-- Name: tamanhos tamanhos_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.tamanhos
    ADD CONSTRAINT tamanhos_pkey PRIMARY KEY (id);


--
-- Name: titulos_pagar titulos_pagar_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.titulos_pagar
    ADD CONSTRAINT titulos_pagar_pkey PRIMARY KEY (id);


--
-- Name: titulos_receber titulos_receber_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.titulos_receber
    ADD CONSTRAINT titulos_receber_pkey PRIMARY KEY (id);


--
-- Name: materiais_fornecedor uk_fornecedor_codigo; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.materiais_fornecedor
    ADD CONSTRAINT uk_fornecedor_codigo UNIQUE (fornecedor_id, codigo_fornecedor);


--
-- Name: grupos_funcionarios uk_grupo_nome; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.grupos_funcionarios
    ADD CONSTRAINT uk_grupo_nome UNIQUE (empresa_id, nome);


--
-- Name: tabela_tempo_padrao uk_tabela_tempo_padrao; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.tabela_tempo_padrao
    ADD CONSTRAINT uk_tabela_tempo_padrao UNIQUE (indice, grau_dificuldade, faixa_comprimento);


--
-- Name: unidades_medida unidades_medida_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.unidades_medida
    ADD CONSTRAINT unidades_medida_pkey PRIMARY KEY (id);


--
-- Name: pacotes uq_tenant_1_pacotes_codigo_barras; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.pacotes
    ADD CONSTRAINT uq_tenant_1_pacotes_codigo_barras UNIQUE (codigo_barras);


--
-- Name: usuario_empresas usuario_empresas_pkey; Type: CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.usuario_empresas
    ADD CONSTRAINT usuario_empresas_pkey PRIMARY KEY (usuario_id, empresa_id);


--
-- Name: apontamentos apontamentos_cupom_id_key; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.apontamentos
    ADD CONSTRAINT apontamentos_cupom_id_key UNIQUE (cupom_id);


--
-- Name: apontamentos_manuais apontamentos_manuais_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_pkey PRIMARY KEY (id);


--
-- Name: apontamentos apontamentos_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.apontamentos
    ADD CONSTRAINT apontamentos_pkey PRIMARY KEY (id);


--
-- Name: categorias categorias_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.categorias
    ADD CONSTRAINT categorias_pkey PRIMARY KEY (id);


--
-- Name: clientes clientes_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.clientes
    ADD CONSTRAINT clientes_pkey PRIMARY KEY (id);


--
-- Name: cores cores_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.cores
    ADD CONSTRAINT cores_pkey PRIMARY KEY (id);


--
-- Name: cupons cupons_codigo_barras_key; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.cupons
    ADD CONSTRAINT cupons_codigo_barras_key UNIQUE (codigo_barras);


--
-- Name: cupons cupons_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.cupons
    ADD CONSTRAINT cupons_pkey PRIMARY KEY (id);


--
-- Name: departamentos departamentos_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.departamentos
    ADD CONSTRAINT departamentos_pkey PRIMARY KEY (id);


--
-- Name: empresas empresas_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.empresas
    ADD CONSTRAINT empresas_pkey PRIMARY KEY (id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_pkey PRIMARY KEY (id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas fichas_tecnicas_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: fornecedores fornecedores_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.fornecedores
    ADD CONSTRAINT fornecedores_pkey PRIMARY KEY (id);


--
-- Name: funcionario_jornadas funcionario_jornadas_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.funcionario_jornadas
    ADD CONSTRAINT funcionario_jornadas_pkey PRIMARY KEY (id);


--
-- Name: funcionarios funcionarios_matricula_key; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.funcionarios
    ADD CONSTRAINT funcionarios_matricula_key UNIQUE (matricula);


--
-- Name: funcionarios funcionarios_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.funcionarios
    ADD CONSTRAINT funcionarios_pkey PRIMARY KEY (id);


--
-- Name: grupos_funcionarios grupos_funcionarios_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.grupos_funcionarios
    ADD CONSTRAINT grupos_funcionarios_pkey PRIMARY KEY (id);


--
-- Name: localizacoes localizacoes_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.localizacoes
    ADD CONSTRAINT localizacoes_pkey PRIMARY KEY (id);


--
-- Name: materiais materiais_codigo_key; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.materiais
    ADD CONSTRAINT materiais_codigo_key UNIQUE (codigo);


--
-- Name: materiais_fornecedor materiais_fornecedor_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.materiais_fornecedor
    ADD CONSTRAINT materiais_fornecedor_pkey PRIMARY KEY (id);


--
-- Name: materiais materiais_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.materiais
    ADD CONSTRAINT materiais_pkey PRIMARY KEY (id);


--
-- Name: ocorrencias ocorrencias_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ocorrencias
    ADD CONSTRAINT ocorrencias_pkey PRIMARY KEY (id);


--
-- Name: ordens_compra_itens ordens_compra_itens_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_compra_itens
    ADD CONSTRAINT ordens_compra_itens_pkey PRIMARY KEY (id);


--
-- Name: ordens_compra ordens_compra_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_compra
    ADD CONSTRAINT ordens_compra_pkey PRIMARY KEY (id);


--
-- Name: ordens_producao_itens ordens_producao_itens_ordem_producao_id_produto_sku_id_key; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_ordem_producao_id_produto_sku_id_key UNIQUE (ordem_producao_id, produto_sku_id);


--
-- Name: ordens_producao_itens ordens_producao_itens_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_pkey PRIMARY KEY (id);


--
-- Name: ordens_producao ordens_producao_numero_key; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_producao
    ADD CONSTRAINT ordens_producao_numero_key UNIQUE (numero);


--
-- Name: ordens_producao ordens_producao_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_producao
    ADD CONSTRAINT ordens_producao_pkey PRIMARY KEY (id);


--
-- Name: pacotes pacotes_ordem_producao_id_produto_sku_id_sequencial_key; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.pacotes
    ADD CONSTRAINT pacotes_ordem_producao_id_produto_sku_id_sequencial_key UNIQUE (ordem_producao_id, produto_sku_id, sequencial);


--
-- Name: pacotes pacotes_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.pacotes
    ADD CONSTRAINT pacotes_pkey PRIMARY KEY (id);


--
-- Name: produtos_base produtos_base_codigo_key; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.produtos_base
    ADD CONSTRAINT produtos_base_codigo_key UNIQUE (codigo);


--
-- Name: produtos_base produtos_base_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.produtos_base
    ADD CONSTRAINT produtos_base_pkey PRIMARY KEY (id);


--
-- Name: produtos_skus produtos_skus_codigo_barras_key; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.produtos_skus
    ADD CONSTRAINT produtos_skus_codigo_barras_key UNIQUE (codigo_barras);


--
-- Name: produtos_skus produtos_skus_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.produtos_skus
    ADD CONSTRAINT produtos_skus_pkey PRIMARY KEY (id);


--
-- Name: produtos_skus produtos_skus_produto_base_id_cor_tamanho_key; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.produtos_skus
    ADD CONSTRAINT produtos_skus_produto_base_id_cor_tamanho_key UNIQUE (produto_base_id, cor_legacy, tamanho_legacy);


--
-- Name: recebimentos_nf_itens recebimentos_nf_itens_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.recebimentos_nf_itens
    ADD CONSTRAINT recebimentos_nf_itens_pkey PRIMARY KEY (id);


--
-- Name: recebimentos_nf recebimentos_nf_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.recebimentos_nf
    ADD CONSTRAINT recebimentos_nf_pkey PRIMARY KEY (id);


--
-- Name: tabela_tempo_padrao tabela_tempo_padrao_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.tabela_tempo_padrao
    ADD CONSTRAINT tabela_tempo_padrao_pkey PRIMARY KEY (id);


--
-- Name: tamanhos tamanhos_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.tamanhos
    ADD CONSTRAINT tamanhos_pkey PRIMARY KEY (id);


--
-- Name: titulos_pagar titulos_pagar_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.titulos_pagar
    ADD CONSTRAINT titulos_pagar_pkey PRIMARY KEY (id);


--
-- Name: titulos_receber titulos_receber_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.titulos_receber
    ADD CONSTRAINT titulos_receber_pkey PRIMARY KEY (id);


--
-- Name: materiais_fornecedor uk_fornecedor_codigo; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.materiais_fornecedor
    ADD CONSTRAINT uk_fornecedor_codigo UNIQUE (fornecedor_id, codigo_fornecedor);


--
-- Name: grupos_funcionarios uk_grupo_nome; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.grupos_funcionarios
    ADD CONSTRAINT uk_grupo_nome UNIQUE (empresa_id, nome);


--
-- Name: tabela_tempo_padrao uk_tabela_tempo_padrao; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.tabela_tempo_padrao
    ADD CONSTRAINT uk_tabela_tempo_padrao UNIQUE (indice, grau_dificuldade, faixa_comprimento);


--
-- Name: unidades_medida unidades_medida_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.unidades_medida
    ADD CONSTRAINT unidades_medida_pkey PRIMARY KEY (id);


--
-- Name: pacotes uq_tenant_c2b84f0e_pacotes_codigo_barras; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.pacotes
    ADD CONSTRAINT uq_tenant_c2b84f0e_pacotes_codigo_barras UNIQUE (codigo_barras);


--
-- Name: usuario_empresas usuario_empresas_pkey; Type: CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.usuario_empresas
    ADD CONSTRAINT usuario_empresas_pkey PRIMARY KEY (usuario_id, empresa_id);


--
-- Name: apontamentos apontamentos_cupom_id_key; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.apontamentos
    ADD CONSTRAINT apontamentos_cupom_id_key UNIQUE (cupom_id);


--
-- Name: apontamentos_manuais apontamentos_manuais_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_pkey PRIMARY KEY (id);


--
-- Name: apontamentos apontamentos_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.apontamentos
    ADD CONSTRAINT apontamentos_pkey PRIMARY KEY (id);


--
-- Name: categorias categorias_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.categorias
    ADD CONSTRAINT categorias_pkey PRIMARY KEY (id);


--
-- Name: clientes clientes_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.clientes
    ADD CONSTRAINT clientes_pkey PRIMARY KEY (id);


--
-- Name: cores cores_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.cores
    ADD CONSTRAINT cores_pkey PRIMARY KEY (id);


--
-- Name: cupons cupons_codigo_barras_key; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.cupons
    ADD CONSTRAINT cupons_codigo_barras_key UNIQUE (codigo_barras);


--
-- Name: cupons cupons_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.cupons
    ADD CONSTRAINT cupons_pkey PRIMARY KEY (id);


--
-- Name: departamentos departamentos_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.departamentos
    ADD CONSTRAINT departamentos_pkey PRIMARY KEY (id);


--
-- Name: empresas empresas_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.empresas
    ADD CONSTRAINT empresas_pkey PRIMARY KEY (id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_pkey PRIMARY KEY (id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_pkey PRIMARY KEY (id);


--
-- Name: fichas_tecnicas fichas_tecnicas_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: fornecedores fornecedores_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.fornecedores
    ADD CONSTRAINT fornecedores_pkey PRIMARY KEY (id);


--
-- Name: funcionario_jornadas funcionario_jornadas_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.funcionario_jornadas
    ADD CONSTRAINT funcionario_jornadas_pkey PRIMARY KEY (id);


--
-- Name: funcionarios funcionarios_matricula_key; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.funcionarios
    ADD CONSTRAINT funcionarios_matricula_key UNIQUE (matricula);


--
-- Name: funcionarios funcionarios_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.funcionarios
    ADD CONSTRAINT funcionarios_pkey PRIMARY KEY (id);


--
-- Name: grupos_funcionarios grupos_funcionarios_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.grupos_funcionarios
    ADD CONSTRAINT grupos_funcionarios_pkey PRIMARY KEY (id);


--
-- Name: localizacoes localizacoes_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.localizacoes
    ADD CONSTRAINT localizacoes_pkey PRIMARY KEY (id);


--
-- Name: materiais materiais_codigo_key; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.materiais
    ADD CONSTRAINT materiais_codigo_key UNIQUE (codigo);


--
-- Name: materiais_fornecedor materiais_fornecedor_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.materiais_fornecedor
    ADD CONSTRAINT materiais_fornecedor_pkey PRIMARY KEY (id);


--
-- Name: materiais materiais_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.materiais
    ADD CONSTRAINT materiais_pkey PRIMARY KEY (id);


--
-- Name: ocorrencias ocorrencias_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ocorrencias
    ADD CONSTRAINT ocorrencias_pkey PRIMARY KEY (id);


--
-- Name: ordens_compra_itens ordens_compra_itens_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_compra_itens
    ADD CONSTRAINT ordens_compra_itens_pkey PRIMARY KEY (id);


--
-- Name: ordens_compra ordens_compra_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_compra
    ADD CONSTRAINT ordens_compra_pkey PRIMARY KEY (id);


--
-- Name: ordens_producao_itens ordens_producao_itens_ordem_producao_id_produto_sku_id_key; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_ordem_producao_id_produto_sku_id_key UNIQUE (ordem_producao_id, produto_sku_id);


--
-- Name: ordens_producao_itens ordens_producao_itens_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_pkey PRIMARY KEY (id);


--
-- Name: ordens_producao ordens_producao_numero_key; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_producao
    ADD CONSTRAINT ordens_producao_numero_key UNIQUE (numero);


--
-- Name: ordens_producao ordens_producao_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_producao
    ADD CONSTRAINT ordens_producao_pkey PRIMARY KEY (id);


--
-- Name: pacotes pacotes_ordem_producao_id_produto_sku_id_sequencial_key; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.pacotes
    ADD CONSTRAINT pacotes_ordem_producao_id_produto_sku_id_sequencial_key UNIQUE (ordem_producao_id, produto_sku_id, sequencial);


--
-- Name: pacotes pacotes_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.pacotes
    ADD CONSTRAINT pacotes_pkey PRIMARY KEY (id);


--
-- Name: produtos_base produtos_base_codigo_key; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.produtos_base
    ADD CONSTRAINT produtos_base_codigo_key UNIQUE (codigo);


--
-- Name: produtos_base produtos_base_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.produtos_base
    ADD CONSTRAINT produtos_base_pkey PRIMARY KEY (id);


--
-- Name: produtos_skus produtos_skus_codigo_barras_key; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.produtos_skus
    ADD CONSTRAINT produtos_skus_codigo_barras_key UNIQUE (codigo_barras);


--
-- Name: produtos_skus produtos_skus_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.produtos_skus
    ADD CONSTRAINT produtos_skus_pkey PRIMARY KEY (id);


--
-- Name: produtos_skus produtos_skus_produto_base_id_cor_tamanho_key; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.produtos_skus
    ADD CONSTRAINT produtos_skus_produto_base_id_cor_tamanho_key UNIQUE (produto_base_id, cor_legacy, tamanho_legacy);


--
-- Name: recebimentos_nf_itens recebimentos_nf_itens_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.recebimentos_nf_itens
    ADD CONSTRAINT recebimentos_nf_itens_pkey PRIMARY KEY (id);


--
-- Name: recebimentos_nf recebimentos_nf_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.recebimentos_nf
    ADD CONSTRAINT recebimentos_nf_pkey PRIMARY KEY (id);


--
-- Name: tabela_tempo_padrao tabela_tempo_padrao_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.tabela_tempo_padrao
    ADD CONSTRAINT tabela_tempo_padrao_pkey PRIMARY KEY (id);


--
-- Name: tamanhos tamanhos_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.tamanhos
    ADD CONSTRAINT tamanhos_pkey PRIMARY KEY (id);


--
-- Name: titulos_pagar titulos_pagar_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.titulos_pagar
    ADD CONSTRAINT titulos_pagar_pkey PRIMARY KEY (id);


--
-- Name: titulos_receber titulos_receber_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.titulos_receber
    ADD CONSTRAINT titulos_receber_pkey PRIMARY KEY (id);


--
-- Name: materiais_fornecedor uk_fornecedor_codigo; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.materiais_fornecedor
    ADD CONSTRAINT uk_fornecedor_codigo UNIQUE (fornecedor_id, codigo_fornecedor);


--
-- Name: grupos_funcionarios uk_grupo_nome; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.grupos_funcionarios
    ADD CONSTRAINT uk_grupo_nome UNIQUE (empresa_id, nome);


--
-- Name: tabela_tempo_padrao uk_tabela_tempo_padrao; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.tabela_tempo_padrao
    ADD CONSTRAINT uk_tabela_tempo_padrao UNIQUE (indice, grau_dificuldade, faixa_comprimento);


--
-- Name: unidades_medida unidades_medida_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.unidades_medida
    ADD CONSTRAINT unidades_medida_pkey PRIMARY KEY (id);


--
-- Name: usuario_empresas usuario_empresas_pkey; Type: CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.usuario_empresas
    ADD CONSTRAINT usuario_empresas_pkey PRIMARY KEY (usuario_id, empresa_id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: master; Owner: -
--

CREATE INDEX flyway_schema_history_s_idx ON master.flyway_schema_history USING btree (success);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: tenant_0afd99ef; Owner: -
--

CREATE INDEX flyway_schema_history_s_idx ON tenant_0afd99ef.flyway_schema_history USING btree (success);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: tenant_1; Owner: -
--

CREATE INDEX flyway_schema_history_s_idx ON tenant_1.flyway_schema_history USING btree (success);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: tenant_c2b84f0e; Owner: -
--

CREATE INDEX flyway_schema_history_s_idx ON tenant_c2b84f0e.flyway_schema_history USING btree (success);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: tenant_df0f12fb; Owner: -
--

CREATE INDEX flyway_schema_history_s_idx ON tenant_df0f12fb.flyway_schema_history USING btree (success);


--
-- Name: acessos_logs acessos_logs_tenant_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.acessos_logs
    ADD CONSTRAINT acessos_logs_tenant_id_fkey FOREIGN KEY (tenant_id) REFERENCES master.clientes_tenant(id);


--
-- Name: acessos_logs acessos_logs_usuario_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.acessos_logs
    ADD CONSTRAINT acessos_logs_usuario_id_fkey FOREIGN KEY (usuario_id) REFERENCES master.usuarios(id);


--
-- Name: faturas_saas faturas_saas_tenant_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.faturas_saas
    ADD CONSTRAINT faturas_saas_tenant_id_fkey FOREIGN KEY (tenant_id) REFERENCES master.clientes_tenant(id);


--
-- Name: faturas_transacoes faturas_transacoes_fatura_saas_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.faturas_transacoes
    ADD CONSTRAINT faturas_transacoes_fatura_saas_id_fkey FOREIGN KEY (fatura_saas_id) REFERENCES master.faturas_saas(id);


--
-- Name: funcionarios fk15hs4n2yk287naj2tgawfi8mv; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.funcionarios
    ADD CONSTRAINT fk15hs4n2yk287naj2tgawfi8mv FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: ordens_producao fk1f6ffrwqqjhuqhn865x3itsh; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.ordens_producao
    ADD CONSTRAINT fk1f6ffrwqqjhuqhn865x3itsh FOREIGN KEY (ficha_tecnica_id) REFERENCES master.fichas_tecnicas(id);


--
-- Name: ordens_producao fk21vxlqb78w63xt9nca9fvvxwj; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.ordens_producao
    ADD CONSTRAINT fk21vxlqb78w63xt9nca9fvvxwj FOREIGN KEY (produto_base_id) REFERENCES master.produtos_base(id);


--
-- Name: estoque_movimentacoes fk24ba0p8mua5epam08yqyfecrt; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.estoque_movimentacoes
    ADD CONSTRAINT fk24ba0p8mua5epam08yqyfecrt FOREIGN KEY (material_id) REFERENCES master.materiais(id);


--
-- Name: tabela_tempo_padrao fk2j5jqwrsbqdfjggjvgtxy1ds1; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.tabela_tempo_padrao
    ADD CONSTRAINT fk2j5jqwrsbqdfjggjvgtxy1ds1 FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: ordens_producao_itens fk48r5fr34dsuo4x36w5v89f6uk; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.ordens_producao_itens
    ADD CONSTRAINT fk48r5fr34dsuo4x36w5v89f6uk FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: ordens_producao fk4k156kgmocu2li3yxa8jgrq50; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.ordens_producao
    ADD CONSTRAINT fk4k156kgmocu2li3yxa8jgrq50 FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: estoque_produtos_movimentacoes fk4pscmgpfvcf1negat512hi7id; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.estoque_produtos_movimentacoes
    ADD CONSTRAINT fk4pscmgpfvcf1negat512hi7id FOREIGN KEY (produto_sku_id) REFERENCES master.produtos_skus(id);


--
-- Name: fichas_tecnicas_operacoes fk4qeqpvwhmk9679v7g76tffjjt; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fichas_tecnicas_operacoes
    ADD CONSTRAINT fk4qeqpvwhmk9679v7g76tffjjt FOREIGN KEY (ficha_tecnica_id) REFERENCES master.fichas_tecnicas(id);


--
-- Name: materiais fk4waq7wl24f196ep022od4nqfs; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.materiais
    ADD CONSTRAINT fk4waq7wl24f196ep022od4nqfs FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: apontamentos fk4x29bwo5kwycubwbace5nr6ah; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.apontamentos
    ADD CONSTRAINT fk4x29bwo5kwycubwbace5nr6ah FOREIGN KEY (funcionario_id) REFERENCES master.funcionarios(id);


--
-- Name: apontamentos fk5fp45qk1w1r6t98x3v2kdg4d9; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.apontamentos
    ADD CONSTRAINT fk5fp45qk1w1r6t98x3v2kdg4d9 FOREIGN KEY (cupom_id) REFERENCES master.cupons(id);


--
-- Name: clientes fk64o9r7jr2f0mdf3l9l4s9lvpn; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.clientes
    ADD CONSTRAINT fk64o9r7jr2f0mdf3l9l4s9lvpn FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: produtos_skus fk6b91age6h0cpxk89ih6k6iqtv; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.produtos_skus
    ADD CONSTRAINT fk6b91age6h0cpxk89ih6k6iqtv FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: estoque_movimentacoes fk6cljd4v9bu4u2uf63sisj0j9l; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.estoque_movimentacoes
    ADD CONSTRAINT fk6cljd4v9bu4u2uf63sisj0j9l FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: ocorrencias fk6igr59apf6ma31a243dcxxef1; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.ocorrencias
    ADD CONSTRAINT fk6igr59apf6ma31a243dcxxef1 FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: funcionarios fk6jijvs3p9d9yo1t6g6v9jmkel; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.funcionarios
    ADD CONSTRAINT fk6jijvs3p9d9yo1t6g6v9jmkel FOREIGN KEY (grupo_id) REFERENCES master.grupos_funcionarios(id);


--
-- Name: pacotes fk6lrtl063wsryak6jq7drpn2mq; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.pacotes
    ADD CONSTRAINT fk6lrtl063wsryak6jq7drpn2mq FOREIGN KEY (produto_sku_id) REFERENCES master.produtos_skus(id);


--
-- Name: titulos_pagar fk8aktc9jte0kbta9y06od4ad7c; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.titulos_pagar
    ADD CONSTRAINT fk8aktc9jte0kbta9y06od4ad7c FOREIGN KEY (funcionario_id) REFERENCES master.funcionarios(id);


--
-- Name: apontamentos fk8eqgh0tw54emyyy52j0syx6uc; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.apontamentos
    ADD CONSTRAINT fk8eqgh0tw54emyyy52j0syx6uc FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: tenant_contracts fk_tenant_contracts_tenant; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.tenant_contracts
    ADD CONSTRAINT fk_tenant_contracts_tenant FOREIGN KEY (tenant_id) REFERENCES master.clientes_tenant(schema_name);


--
-- Name: tenant_invoices fk_tenant_invoices_contract; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.tenant_invoices
    ADD CONSTRAINT fk_tenant_invoices_contract FOREIGN KEY (contract_id) REFERENCES master.tenant_contracts(id);


--
-- Name: tenant_modules fk_tenant_modules_tenant; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.tenant_modules
    ADD CONSTRAINT fk_tenant_modules_tenant FOREIGN KEY (tenant_id) REFERENCES master.clientes_tenant(schema_name);


--
-- Name: usuario_permissoes fk_usuario_permissoes_usuario; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.usuario_permissoes
    ADD CONSTRAINT fk_usuario_permissoes_usuario FOREIGN KEY (usuario_id) REFERENCES master.usuarios(id) ON DELETE CASCADE;


--
-- Name: usuarios fk_usuario_tenant; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.usuarios
    ADD CONSTRAINT fk_usuario_tenant FOREIGN KEY (tenant_id) REFERENCES master.clientes_tenant(schema_name);


--
-- Name: produtos_base fka8caolng8gx6r7so4alry6eii; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.produtos_base
    ADD CONSTRAINT fka8caolng8gx6r7so4alry6eii FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: funcionario_jornadas fkag7ugpyfqo5gtt46srh0bben4; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.funcionario_jornadas
    ADD CONSTRAINT fkag7ugpyfqo5gtt46srh0bben4 FOREIGN KEY (funcionario_id) REFERENCES master.funcionarios(id);


--
-- Name: estoque_produtos_movimentacoes fkck3vdshvo7kty2u7p29llpy2; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.estoque_produtos_movimentacoes
    ADD CONSTRAINT fkck3vdshvo7kty2u7p29llpy2 FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: usuario_empresas fkd0flvas0yau981hjwtiwjoin6; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.usuario_empresas
    ADD CONSTRAINT fkd0flvas0yau981hjwtiwjoin6 FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: fichas_tecnicas_materiais fkdiqx5y28l15ymvr8dm9hkssxs; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fichas_tecnicas_materiais
    ADD CONSTRAINT fkdiqx5y28l15ymvr8dm9hkssxs FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: unidades_medida fkdtgt1i4n3pmv6soe9eycqt8e6; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.unidades_medida
    ADD CONSTRAINT fkdtgt1i4n3pmv6soe9eycqt8e6 FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: categorias fkeajwtqjo8juq46nnikqjyntkq; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.categorias
    ADD CONSTRAINT fkeajwtqjo8juq46nnikqjyntkq FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: cupons fkeso49bcmsw0mphtlfhdsn14wl; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.cupons
    ADD CONSTRAINT fkeso49bcmsw0mphtlfhdsn14wl FOREIGN KEY (pacote_id) REFERENCES master.pacotes(id);


--
-- Name: cupons fkfufvsu46pufyiuwb1p435gqhu; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.cupons
    ADD CONSTRAINT fkfufvsu46pufyiuwb1p435gqhu FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: produtos_skus fkg3ne9ktrsr4s6otr9gr7gpcfx; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.produtos_skus
    ADD CONSTRAINT fkg3ne9ktrsr4s6otr9gr7gpcfx FOREIGN KEY (produto_base_id) REFERENCES master.produtos_base(id);


--
-- Name: fornecedores fkgkx5trl1bk7yhqtx7vnm2268i; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fornecedores
    ADD CONSTRAINT fkgkx5trl1bk7yhqtx7vnm2268i FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: pacotes fkhd8r1ex329c32lphlro6ivunw; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.pacotes
    ADD CONSTRAINT fkhd8r1ex329c32lphlro6ivunw FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: pacotes fkix8k3njau8n6j70aok70dk0pn; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.pacotes
    ADD CONSTRAINT fkix8k3njau8n6j70aok70dk0pn FOREIGN KEY (ordem_producao_id) REFERENCES master.ordens_producao(id);


--
-- Name: apontamentos_manuais fkk0rrpj8dby4aiewww9clvkyv8; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.apontamentos_manuais
    ADD CONSTRAINT fkk0rrpj8dby4aiewww9clvkyv8 FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: titulos_pagar fkkiv8psxx78td0yc3hrpsergyw; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.titulos_pagar
    ADD CONSTRAINT fkkiv8psxx78td0yc3hrpsergyw FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: tamanhos fklnyrlo3kvf17giq8c0yeprwok; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.tamanhos
    ADD CONSTRAINT fklnyrlo3kvf17giq8c0yeprwok FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: apontamentos_manuais fknd99g2blewh5k35lwimss0qvk; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.apontamentos_manuais
    ADD CONSTRAINT fknd99g2blewh5k35lwimss0qvk FOREIGN KEY (funcionario_id) REFERENCES master.funcionarios(id);


--
-- Name: ordens_producao_itens fknie2ccm097fasulowjwg3wlkc; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.ordens_producao_itens
    ADD CONSTRAINT fknie2ccm097fasulowjwg3wlkc FOREIGN KEY (ordem_producao_id) REFERENCES master.ordens_producao(id);


--
-- Name: fichas_tecnicas_materiais fko6wfm2l6830iwq5eg9ehqae3t; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fichas_tecnicas_materiais
    ADD CONSTRAINT fko6wfm2l6830iwq5eg9ehqae3t FOREIGN KEY (material_id) REFERENCES master.materiais(id);


--
-- Name: ocorrencias fkowo0juecsc5opschfr8anywsv; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.ocorrencias
    ADD CONSTRAINT fkowo0juecsc5opschfr8anywsv FOREIGN KEY (funcionario_id) REFERENCES master.funcionarios(id);


--
-- Name: fichas_tecnicas_materiais fkoyqq0n2d2y4gym1pdca2fkqnm; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fichas_tecnicas_materiais
    ADD CONSTRAINT fkoyqq0n2d2y4gym1pdca2fkqnm FOREIGN KEY (ficha_tecnica_id) REFERENCES master.fichas_tecnicas(id);


--
-- Name: fichas_tecnicas_operacoes fkqu1cga12ikosepfn67gof8xju; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fichas_tecnicas_operacoes
    ADD CONSTRAINT fkqu1cga12ikosepfn67gof8xju FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: ordens_producao_itens fkrnylmym6boy1ycjqedey05jwd; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.ordens_producao_itens
    ADD CONSTRAINT fkrnylmym6boy1ycjqedey05jwd FOREIGN KEY (produto_sku_id) REFERENCES master.produtos_skus(id);


--
-- Name: cupons fks7xq1sp5h3mnt1r1f17jig4t4; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.cupons
    ADD CONSTRAINT fks7xq1sp5h3mnt1r1f17jig4t4 FOREIGN KEY (operacao_id) REFERENCES master.fichas_tecnicas_operacoes(id);


--
-- Name: cores fkslx797xfa0xislxeovya1fuqf; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.cores
    ADD CONSTRAINT fkslx797xfa0xislxeovya1fuqf FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: grupos_funcionarios fkspefpoj5bp6bl1m8rl4qfafqk; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.grupos_funcionarios
    ADD CONSTRAINT fkspefpoj5bp6bl1m8rl4qfafqk FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: fichas_tecnicas fksvkg3uu9epo126rkloye1bmxy; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fichas_tecnicas
    ADD CONSTRAINT fksvkg3uu9epo126rkloye1bmxy FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: fichas_tecnicas fktigfy9281cl0sp39egw1kbwew; Type: FK CONSTRAINT; Schema: master; Owner: -
--

ALTER TABLE ONLY master.fichas_tecnicas
    ADD CONSTRAINT fktigfy9281cl0sp39egw1kbwew FOREIGN KEY (produto_base_id) REFERENCES master.produtos_base(id);


--
-- Name: apontamentos apontamentos_cupom_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.apontamentos
    ADD CONSTRAINT apontamentos_cupom_id_fkey FOREIGN KEY (cupom_id) REFERENCES tenant_0afd99ef.cupons(id);


--
-- Name: apontamentos apontamentos_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.apontamentos
    ADD CONSTRAINT apontamentos_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: apontamentos apontamentos_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.apontamentos
    ADD CONSTRAINT apontamentos_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_0afd99ef.funcionarios(id);


--
-- Name: apontamentos_manuais apontamentos_manuais_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: apontamentos_manuais apontamentos_manuais_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_0afd99ef.funcionarios(id);


--
-- Name: categorias categorias_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.categorias
    ADD CONSTRAINT categorias_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: clientes clientes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.clientes
    ADD CONSTRAINT clientes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: cupons cupons_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.cupons
    ADD CONSTRAINT cupons_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: cupons cupons_operacao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.cupons
    ADD CONSTRAINT cupons_operacao_id_fkey FOREIGN KEY (operacao_id) REFERENCES tenant_0afd99ef.fichas_tecnicas_operacoes(id);


--
-- Name: cupons cupons_pacote_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.cupons
    ADD CONSTRAINT cupons_pacote_id_fkey FOREIGN KEY (pacote_id) REFERENCES tenant_0afd99ef.pacotes(id);


--
-- Name: departamentos departamentos_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.departamentos
    ADD CONSTRAINT departamentos_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_material_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_material_id_fkey FOREIGN KEY (material_id) REFERENCES tenant_0afd99ef.materiais(id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_produto_sku_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_produto_sku_id_fkey FOREIGN KEY (produto_sku_id) REFERENCES tenant_0afd99ef.produtos_skus(id);


--
-- Name: fichas_tecnicas fichas_tecnicas_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_ficha_tecnica_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_ficha_tecnica_id_fkey FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_0afd99ef.fichas_tecnicas(id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_material_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_material_id_fkey FOREIGN KEY (material_id) REFERENCES tenant_0afd99ef.materiais(id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_ficha_tecnica_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_ficha_tecnica_id_fkey FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_0afd99ef.fichas_tecnicas(id);


--
-- Name: fichas_tecnicas fichas_tecnicas_produto_base_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_produto_base_id_fkey FOREIGN KEY (produto_base_id) REFERENCES tenant_0afd99ef.produtos_base(id);


--
-- Name: cores fk_cores_empresa; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.cores
    ADD CONSTRAINT fk_cores_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: funcionarios fk_funcionarios_grupo; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.funcionarios
    ADD CONSTRAINT fk_funcionarios_grupo FOREIGN KEY (grupo_id) REFERENCES tenant_0afd99ef.grupos_funcionarios(id);


--
-- Name: funcionario_jornadas fk_jornada_funcionario; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.funcionario_jornadas
    ADD CONSTRAINT fk_jornada_funcionario FOREIGN KEY (funcionario_id) REFERENCES tenant_0afd99ef.funcionarios(id) ON DELETE CASCADE;


--
-- Name: materiais_fornecedor fk_matforn_empresa; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.materiais_fornecedor
    ADD CONSTRAINT fk_matforn_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: materiais_fornecedor fk_matforn_fornecedor; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.materiais_fornecedor
    ADD CONSTRAINT fk_matforn_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tenant_0afd99ef.fornecedores(id);


--
-- Name: materiais_fornecedor fk_matforn_material; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.materiais_fornecedor
    ADD CONSTRAINT fk_matforn_material FOREIGN KEY (material_id) REFERENCES tenant_0afd99ef.materiais(id);


--
-- Name: ordens_compra fk_oc_empresa; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_compra
    ADD CONSTRAINT fk_oc_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: ordens_compra fk_oc_fornecedor; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_compra
    ADD CONSTRAINT fk_oc_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tenant_0afd99ef.fornecedores(id);


--
-- Name: ordens_compra_itens fk_oci_material; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_compra_itens
    ADD CONSTRAINT fk_oci_material FOREIGN KEY (material_id) REFERENCES tenant_0afd99ef.materiais(id);


--
-- Name: ordens_compra_itens fk_oci_oc; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_compra_itens
    ADD CONSTRAINT fk_oci_oc FOREIGN KEY (ordem_compra_id) REFERENCES tenant_0afd99ef.ordens_compra(id) ON DELETE CASCADE;


--
-- Name: recebimentos_nf fk_rnf_empresa; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.recebimentos_nf
    ADD CONSTRAINT fk_rnf_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: recebimentos_nf fk_rnf_fornecedor; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.recebimentos_nf
    ADD CONSTRAINT fk_rnf_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tenant_0afd99ef.fornecedores(id);


--
-- Name: recebimentos_nf fk_rnf_oc; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.recebimentos_nf
    ADD CONSTRAINT fk_rnf_oc FOREIGN KEY (ordem_compra_id) REFERENCES tenant_0afd99ef.ordens_compra(id);


--
-- Name: recebimentos_nf_itens fk_rnfi_material; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.recebimentos_nf_itens
    ADD CONSTRAINT fk_rnfi_material FOREIGN KEY (material_id) REFERENCES tenant_0afd99ef.materiais(id);


--
-- Name: recebimentos_nf_itens fk_rnfi_rnf; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.recebimentos_nf_itens
    ADD CONSTRAINT fk_rnfi_rnf FOREIGN KEY (recebimento_nf_id) REFERENCES tenant_0afd99ef.recebimentos_nf(id) ON DELETE CASCADE;


--
-- Name: tamanhos fk_tamanhos_empresa; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.tamanhos
    ADD CONSTRAINT fk_tamanhos_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: fornecedores fornecedores_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.fornecedores
    ADD CONSTRAINT fornecedores_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: funcionarios funcionarios_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.funcionarios
    ADD CONSTRAINT funcionarios_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: localizacoes localizacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.localizacoes
    ADD CONSTRAINT localizacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: materiais materiais_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.materiais
    ADD CONSTRAINT materiais_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: materiais materiais_fornecedor_padrao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.materiais
    ADD CONSTRAINT materiais_fornecedor_padrao_id_fkey FOREIGN KEY (fornecedor_padrao_id) REFERENCES tenant_0afd99ef.fornecedores(id);


--
-- Name: materiais materiais_unidade_medida_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.materiais
    ADD CONSTRAINT materiais_unidade_medida_id_fkey FOREIGN KEY (unidade_medida_id) REFERENCES tenant_0afd99ef.unidades_medida(id);


--
-- Name: ocorrencias ocorrencias_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ocorrencias
    ADD CONSTRAINT ocorrencias_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: ocorrencias ocorrencias_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ocorrencias
    ADD CONSTRAINT ocorrencias_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_0afd99ef.funcionarios(id);


--
-- Name: ordens_producao ordens_producao_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_producao
    ADD CONSTRAINT ordens_producao_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: ordens_producao ordens_producao_ficha_tecnica_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_producao
    ADD CONSTRAINT ordens_producao_ficha_tecnica_id_fkey FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_0afd99ef.fichas_tecnicas(id);


--
-- Name: ordens_producao_itens ordens_producao_itens_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: ordens_producao_itens ordens_producao_itens_ordem_producao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_ordem_producao_id_fkey FOREIGN KEY (ordem_producao_id) REFERENCES tenant_0afd99ef.ordens_producao(id);


--
-- Name: ordens_producao_itens ordens_producao_itens_produto_sku_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_produto_sku_id_fkey FOREIGN KEY (produto_sku_id) REFERENCES tenant_0afd99ef.produtos_skus(id);


--
-- Name: ordens_producao ordens_producao_produto_base_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.ordens_producao
    ADD CONSTRAINT ordens_producao_produto_base_id_fkey FOREIGN KEY (produto_base_id) REFERENCES tenant_0afd99ef.produtos_base(id);


--
-- Name: pacotes pacotes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.pacotes
    ADD CONSTRAINT pacotes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: pacotes pacotes_ordem_producao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.pacotes
    ADD CONSTRAINT pacotes_ordem_producao_id_fkey FOREIGN KEY (ordem_producao_id) REFERENCES tenant_0afd99ef.ordens_producao(id);


--
-- Name: pacotes pacotes_produto_sku_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.pacotes
    ADD CONSTRAINT pacotes_produto_sku_id_fkey FOREIGN KEY (produto_sku_id) REFERENCES tenant_0afd99ef.produtos_skus(id);


--
-- Name: produtos_base produtos_base_categoria_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.produtos_base
    ADD CONSTRAINT produtos_base_categoria_id_fkey FOREIGN KEY (categoria_id) REFERENCES tenant_0afd99ef.categorias(id);


--
-- Name: produtos_base produtos_base_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.produtos_base
    ADD CONSTRAINT produtos_base_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: produtos_skus produtos_skus_cor_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.produtos_skus
    ADD CONSTRAINT produtos_skus_cor_id_fkey FOREIGN KEY (cor_id) REFERENCES tenant_0afd99ef.cores(id);


--
-- Name: produtos_skus produtos_skus_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.produtos_skus
    ADD CONSTRAINT produtos_skus_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: produtos_skus produtos_skus_produto_base_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.produtos_skus
    ADD CONSTRAINT produtos_skus_produto_base_id_fkey FOREIGN KEY (produto_base_id) REFERENCES tenant_0afd99ef.produtos_base(id);


--
-- Name: produtos_skus produtos_skus_tamanho_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.produtos_skus
    ADD CONSTRAINT produtos_skus_tamanho_id_fkey FOREIGN KEY (tamanho_id) REFERENCES tenant_0afd99ef.tamanhos(id);


--
-- Name: tabela_tempo_padrao tabela_tempo_padrao_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.tabela_tempo_padrao
    ADD CONSTRAINT tabela_tempo_padrao_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: titulos_pagar titulos_pagar_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.titulos_pagar
    ADD CONSTRAINT titulos_pagar_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: titulos_pagar titulos_pagar_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.titulos_pagar
    ADD CONSTRAINT titulos_pagar_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_0afd99ef.funcionarios(id);


--
-- Name: titulos_receber titulos_receber_cliente_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.titulos_receber
    ADD CONSTRAINT titulos_receber_cliente_id_fkey FOREIGN KEY (cliente_id) REFERENCES tenant_0afd99ef.clientes(id);


--
-- Name: titulos_receber titulos_receber_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.titulos_receber
    ADD CONSTRAINT titulos_receber_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: unidades_medida unidades_medida_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.unidades_medida
    ADD CONSTRAINT unidades_medida_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: usuario_empresas usuario_empresas_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_0afd99ef; Owner: -
--

ALTER TABLE ONLY tenant_0afd99ef.usuario_empresas
    ADD CONSTRAINT usuario_empresas_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_0afd99ef.empresas(id);


--
-- Name: apontamentos apontamentos_cupom_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.apontamentos
    ADD CONSTRAINT apontamentos_cupom_id_fkey FOREIGN KEY (cupom_id) REFERENCES tenant_1.cupons(id);


--
-- Name: apontamentos apontamentos_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.apontamentos
    ADD CONSTRAINT apontamentos_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: apontamentos apontamentos_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.apontamentos
    ADD CONSTRAINT apontamentos_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_1.funcionarios(id);


--
-- Name: apontamentos_manuais apontamentos_manuais_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: apontamentos_manuais apontamentos_manuais_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_1.funcionarios(id);


--
-- Name: categorias categorias_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.categorias
    ADD CONSTRAINT categorias_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: clientes clientes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.clientes
    ADD CONSTRAINT clientes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: cupons cupons_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.cupons
    ADD CONSTRAINT cupons_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: cupons cupons_operacao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.cupons
    ADD CONSTRAINT cupons_operacao_id_fkey FOREIGN KEY (operacao_id) REFERENCES tenant_1.fichas_tecnicas_operacoes(id);


--
-- Name: cupons cupons_pacote_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.cupons
    ADD CONSTRAINT cupons_pacote_id_fkey FOREIGN KEY (pacote_id) REFERENCES tenant_1.pacotes(id);


--
-- Name: departamentos departamentos_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.departamentos
    ADD CONSTRAINT departamentos_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_material_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_material_id_fkey FOREIGN KEY (material_id) REFERENCES tenant_1.materiais(id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_produto_sku_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_produto_sku_id_fkey FOREIGN KEY (produto_sku_id) REFERENCES tenant_1.produtos_skus(id);


--
-- Name: fichas_tecnicas fichas_tecnicas_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_ficha_tecnica_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_ficha_tecnica_id_fkey FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_1.fichas_tecnicas(id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_material_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_material_id_fkey FOREIGN KEY (material_id) REFERENCES tenant_1.materiais(id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_ficha_tecnica_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_ficha_tecnica_id_fkey FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_1.fichas_tecnicas(id);


--
-- Name: fichas_tecnicas fichas_tecnicas_produto_base_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_produto_base_id_fkey FOREIGN KEY (produto_base_id) REFERENCES tenant_1.produtos_base(id);


--
-- Name: cores fk_cores_empresa; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.cores
    ADD CONSTRAINT fk_cores_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: funcionarios fk_funcionarios_grupo; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.funcionarios
    ADD CONSTRAINT fk_funcionarios_grupo FOREIGN KEY (grupo_id) REFERENCES tenant_1.grupos_funcionarios(id);


--
-- Name: funcionario_jornadas fk_jornada_funcionario; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.funcionario_jornadas
    ADD CONSTRAINT fk_jornada_funcionario FOREIGN KEY (funcionario_id) REFERENCES tenant_1.funcionarios(id) ON DELETE CASCADE;


--
-- Name: materiais_fornecedor fk_matforn_empresa; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.materiais_fornecedor
    ADD CONSTRAINT fk_matforn_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: materiais_fornecedor fk_matforn_fornecedor; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.materiais_fornecedor
    ADD CONSTRAINT fk_matforn_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tenant_1.fornecedores(id);


--
-- Name: materiais_fornecedor fk_matforn_material; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.materiais_fornecedor
    ADD CONSTRAINT fk_matforn_material FOREIGN KEY (material_id) REFERENCES tenant_1.materiais(id);


--
-- Name: ordens_compra fk_oc_empresa; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_compra
    ADD CONSTRAINT fk_oc_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: ordens_compra fk_oc_fornecedor; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_compra
    ADD CONSTRAINT fk_oc_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tenant_1.fornecedores(id);


--
-- Name: ordens_compra_itens fk_oci_material; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_compra_itens
    ADD CONSTRAINT fk_oci_material FOREIGN KEY (material_id) REFERENCES tenant_1.materiais(id);


--
-- Name: ordens_compra_itens fk_oci_oc; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_compra_itens
    ADD CONSTRAINT fk_oci_oc FOREIGN KEY (ordem_compra_id) REFERENCES tenant_1.ordens_compra(id) ON DELETE CASCADE;


--
-- Name: recebimentos_nf fk_rnf_empresa; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.recebimentos_nf
    ADD CONSTRAINT fk_rnf_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: recebimentos_nf fk_rnf_fornecedor; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.recebimentos_nf
    ADD CONSTRAINT fk_rnf_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tenant_1.fornecedores(id);


--
-- Name: recebimentos_nf fk_rnf_oc; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.recebimentos_nf
    ADD CONSTRAINT fk_rnf_oc FOREIGN KEY (ordem_compra_id) REFERENCES tenant_1.ordens_compra(id);


--
-- Name: recebimentos_nf_itens fk_rnfi_material; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.recebimentos_nf_itens
    ADD CONSTRAINT fk_rnfi_material FOREIGN KEY (material_id) REFERENCES tenant_1.materiais(id);


--
-- Name: recebimentos_nf_itens fk_rnfi_rnf; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.recebimentos_nf_itens
    ADD CONSTRAINT fk_rnfi_rnf FOREIGN KEY (recebimento_nf_id) REFERENCES tenant_1.recebimentos_nf(id) ON DELETE CASCADE;


--
-- Name: tamanhos fk_tamanhos_empresa; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.tamanhos
    ADD CONSTRAINT fk_tamanhos_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: fornecedores fornecedores_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.fornecedores
    ADD CONSTRAINT fornecedores_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: funcionarios funcionarios_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.funcionarios
    ADD CONSTRAINT funcionarios_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: localizacoes localizacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.localizacoes
    ADD CONSTRAINT localizacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: materiais materiais_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.materiais
    ADD CONSTRAINT materiais_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: materiais materiais_fornecedor_padrao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.materiais
    ADD CONSTRAINT materiais_fornecedor_padrao_id_fkey FOREIGN KEY (fornecedor_padrao_id) REFERENCES tenant_1.fornecedores(id);


--
-- Name: materiais materiais_unidade_medida_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.materiais
    ADD CONSTRAINT materiais_unidade_medida_id_fkey FOREIGN KEY (unidade_medida_id) REFERENCES tenant_1.unidades_medida(id);


--
-- Name: ocorrencias ocorrencias_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ocorrencias
    ADD CONSTRAINT ocorrencias_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: ocorrencias ocorrencias_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ocorrencias
    ADD CONSTRAINT ocorrencias_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_1.funcionarios(id);


--
-- Name: ordens_producao ordens_producao_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_producao
    ADD CONSTRAINT ordens_producao_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: ordens_producao ordens_producao_ficha_tecnica_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_producao
    ADD CONSTRAINT ordens_producao_ficha_tecnica_id_fkey FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_1.fichas_tecnicas(id);


--
-- Name: ordens_producao_itens ordens_producao_itens_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: ordens_producao_itens ordens_producao_itens_ordem_producao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_ordem_producao_id_fkey FOREIGN KEY (ordem_producao_id) REFERENCES tenant_1.ordens_producao(id);


--
-- Name: ordens_producao_itens ordens_producao_itens_produto_sku_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_produto_sku_id_fkey FOREIGN KEY (produto_sku_id) REFERENCES tenant_1.produtos_skus(id);


--
-- Name: ordens_producao ordens_producao_produto_base_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.ordens_producao
    ADD CONSTRAINT ordens_producao_produto_base_id_fkey FOREIGN KEY (produto_base_id) REFERENCES tenant_1.produtos_base(id);


--
-- Name: pacotes pacotes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.pacotes
    ADD CONSTRAINT pacotes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: pacotes pacotes_ordem_producao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.pacotes
    ADD CONSTRAINT pacotes_ordem_producao_id_fkey FOREIGN KEY (ordem_producao_id) REFERENCES tenant_1.ordens_producao(id);


--
-- Name: pacotes pacotes_produto_sku_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.pacotes
    ADD CONSTRAINT pacotes_produto_sku_id_fkey FOREIGN KEY (produto_sku_id) REFERENCES tenant_1.produtos_skus(id);


--
-- Name: produtos_base produtos_base_categoria_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.produtos_base
    ADD CONSTRAINT produtos_base_categoria_id_fkey FOREIGN KEY (categoria_id) REFERENCES tenant_1.categorias(id);


--
-- Name: produtos_base produtos_base_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.produtos_base
    ADD CONSTRAINT produtos_base_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: produtos_skus produtos_skus_cor_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.produtos_skus
    ADD CONSTRAINT produtos_skus_cor_id_fkey FOREIGN KEY (cor_id) REFERENCES tenant_1.cores(id);


--
-- Name: produtos_skus produtos_skus_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.produtos_skus
    ADD CONSTRAINT produtos_skus_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: produtos_skus produtos_skus_produto_base_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.produtos_skus
    ADD CONSTRAINT produtos_skus_produto_base_id_fkey FOREIGN KEY (produto_base_id) REFERENCES tenant_1.produtos_base(id);


--
-- Name: produtos_skus produtos_skus_tamanho_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.produtos_skus
    ADD CONSTRAINT produtos_skus_tamanho_id_fkey FOREIGN KEY (tamanho_id) REFERENCES tenant_1.tamanhos(id);


--
-- Name: tabela_tempo_padrao tabela_tempo_padrao_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.tabela_tempo_padrao
    ADD CONSTRAINT tabela_tempo_padrao_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: titulos_pagar titulos_pagar_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.titulos_pagar
    ADD CONSTRAINT titulos_pagar_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: titulos_pagar titulos_pagar_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.titulos_pagar
    ADD CONSTRAINT titulos_pagar_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_1.funcionarios(id);


--
-- Name: titulos_receber titulos_receber_cliente_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.titulos_receber
    ADD CONSTRAINT titulos_receber_cliente_id_fkey FOREIGN KEY (cliente_id) REFERENCES tenant_1.clientes(id);


--
-- Name: titulos_receber titulos_receber_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.titulos_receber
    ADD CONSTRAINT titulos_receber_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: unidades_medida unidades_medida_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.unidades_medida
    ADD CONSTRAINT unidades_medida_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: usuario_empresas usuario_empresas_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_1; Owner: -
--

ALTER TABLE ONLY tenant_1.usuario_empresas
    ADD CONSTRAINT usuario_empresas_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_1.empresas(id);


--
-- Name: apontamentos apontamentos_cupom_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.apontamentos
    ADD CONSTRAINT apontamentos_cupom_id_fkey FOREIGN KEY (cupom_id) REFERENCES tenant_c2b84f0e.cupons(id);


--
-- Name: apontamentos apontamentos_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.apontamentos
    ADD CONSTRAINT apontamentos_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: apontamentos apontamentos_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.apontamentos
    ADD CONSTRAINT apontamentos_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_c2b84f0e.funcionarios(id);


--
-- Name: apontamentos_manuais apontamentos_manuais_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: apontamentos_manuais apontamentos_manuais_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_c2b84f0e.funcionarios(id);


--
-- Name: categorias categorias_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.categorias
    ADD CONSTRAINT categorias_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: clientes clientes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.clientes
    ADD CONSTRAINT clientes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: cupons cupons_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.cupons
    ADD CONSTRAINT cupons_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: cupons cupons_operacao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.cupons
    ADD CONSTRAINT cupons_operacao_id_fkey FOREIGN KEY (operacao_id) REFERENCES tenant_c2b84f0e.fichas_tecnicas_operacoes(id);


--
-- Name: cupons cupons_pacote_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.cupons
    ADD CONSTRAINT cupons_pacote_id_fkey FOREIGN KEY (pacote_id) REFERENCES tenant_c2b84f0e.pacotes(id);


--
-- Name: departamentos departamentos_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.departamentos
    ADD CONSTRAINT departamentos_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_material_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_material_id_fkey FOREIGN KEY (material_id) REFERENCES tenant_c2b84f0e.materiais(id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_produto_sku_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_produto_sku_id_fkey FOREIGN KEY (produto_sku_id) REFERENCES tenant_c2b84f0e.produtos_skus(id);


--
-- Name: fichas_tecnicas fichas_tecnicas_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_ficha_tecnica_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_ficha_tecnica_id_fkey FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_c2b84f0e.fichas_tecnicas(id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_material_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_material_id_fkey FOREIGN KEY (material_id) REFERENCES tenant_c2b84f0e.materiais(id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_ficha_tecnica_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_ficha_tecnica_id_fkey FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_c2b84f0e.fichas_tecnicas(id);


--
-- Name: fichas_tecnicas fichas_tecnicas_produto_base_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_produto_base_id_fkey FOREIGN KEY (produto_base_id) REFERENCES tenant_c2b84f0e.produtos_base(id);


--
-- Name: cores fk_cores_empresa; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.cores
    ADD CONSTRAINT fk_cores_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: funcionarios fk_funcionarios_grupo; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.funcionarios
    ADD CONSTRAINT fk_funcionarios_grupo FOREIGN KEY (grupo_id) REFERENCES tenant_c2b84f0e.grupos_funcionarios(id);


--
-- Name: funcionario_jornadas fk_jornada_funcionario; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.funcionario_jornadas
    ADD CONSTRAINT fk_jornada_funcionario FOREIGN KEY (funcionario_id) REFERENCES tenant_c2b84f0e.funcionarios(id) ON DELETE CASCADE;


--
-- Name: materiais_fornecedor fk_matforn_empresa; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.materiais_fornecedor
    ADD CONSTRAINT fk_matforn_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: materiais_fornecedor fk_matforn_fornecedor; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.materiais_fornecedor
    ADD CONSTRAINT fk_matforn_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tenant_c2b84f0e.fornecedores(id);


--
-- Name: materiais_fornecedor fk_matforn_material; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.materiais_fornecedor
    ADD CONSTRAINT fk_matforn_material FOREIGN KEY (material_id) REFERENCES tenant_c2b84f0e.materiais(id);


--
-- Name: ordens_compra fk_oc_empresa; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_compra
    ADD CONSTRAINT fk_oc_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: ordens_compra fk_oc_fornecedor; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_compra
    ADD CONSTRAINT fk_oc_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tenant_c2b84f0e.fornecedores(id);


--
-- Name: ordens_compra_itens fk_oci_material; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_compra_itens
    ADD CONSTRAINT fk_oci_material FOREIGN KEY (material_id) REFERENCES tenant_c2b84f0e.materiais(id);


--
-- Name: ordens_compra_itens fk_oci_oc; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_compra_itens
    ADD CONSTRAINT fk_oci_oc FOREIGN KEY (ordem_compra_id) REFERENCES tenant_c2b84f0e.ordens_compra(id) ON DELETE CASCADE;


--
-- Name: recebimentos_nf fk_rnf_empresa; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.recebimentos_nf
    ADD CONSTRAINT fk_rnf_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: recebimentos_nf fk_rnf_fornecedor; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.recebimentos_nf
    ADD CONSTRAINT fk_rnf_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tenant_c2b84f0e.fornecedores(id);


--
-- Name: recebimentos_nf fk_rnf_oc; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.recebimentos_nf
    ADD CONSTRAINT fk_rnf_oc FOREIGN KEY (ordem_compra_id) REFERENCES tenant_c2b84f0e.ordens_compra(id);


--
-- Name: recebimentos_nf_itens fk_rnfi_material; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.recebimentos_nf_itens
    ADD CONSTRAINT fk_rnfi_material FOREIGN KEY (material_id) REFERENCES tenant_c2b84f0e.materiais(id);


--
-- Name: recebimentos_nf_itens fk_rnfi_rnf; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.recebimentos_nf_itens
    ADD CONSTRAINT fk_rnfi_rnf FOREIGN KEY (recebimento_nf_id) REFERENCES tenant_c2b84f0e.recebimentos_nf(id) ON DELETE CASCADE;


--
-- Name: tamanhos fk_tamanhos_empresa; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.tamanhos
    ADD CONSTRAINT fk_tamanhos_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: fornecedores fornecedores_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.fornecedores
    ADD CONSTRAINT fornecedores_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: funcionarios funcionarios_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.funcionarios
    ADD CONSTRAINT funcionarios_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: localizacoes localizacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.localizacoes
    ADD CONSTRAINT localizacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: materiais materiais_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.materiais
    ADD CONSTRAINT materiais_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: materiais materiais_fornecedor_padrao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.materiais
    ADD CONSTRAINT materiais_fornecedor_padrao_id_fkey FOREIGN KEY (fornecedor_padrao_id) REFERENCES tenant_c2b84f0e.fornecedores(id);


--
-- Name: materiais materiais_unidade_medida_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.materiais
    ADD CONSTRAINT materiais_unidade_medida_id_fkey FOREIGN KEY (unidade_medida_id) REFERENCES tenant_c2b84f0e.unidades_medida(id);


--
-- Name: ocorrencias ocorrencias_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ocorrencias
    ADD CONSTRAINT ocorrencias_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: ocorrencias ocorrencias_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ocorrencias
    ADD CONSTRAINT ocorrencias_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_c2b84f0e.funcionarios(id);


--
-- Name: ordens_producao ordens_producao_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_producao
    ADD CONSTRAINT ordens_producao_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: ordens_producao ordens_producao_ficha_tecnica_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_producao
    ADD CONSTRAINT ordens_producao_ficha_tecnica_id_fkey FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_c2b84f0e.fichas_tecnicas(id);


--
-- Name: ordens_producao_itens ordens_producao_itens_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: ordens_producao_itens ordens_producao_itens_ordem_producao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_ordem_producao_id_fkey FOREIGN KEY (ordem_producao_id) REFERENCES tenant_c2b84f0e.ordens_producao(id);


--
-- Name: ordens_producao_itens ordens_producao_itens_produto_sku_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_produto_sku_id_fkey FOREIGN KEY (produto_sku_id) REFERENCES tenant_c2b84f0e.produtos_skus(id);


--
-- Name: ordens_producao ordens_producao_produto_base_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.ordens_producao
    ADD CONSTRAINT ordens_producao_produto_base_id_fkey FOREIGN KEY (produto_base_id) REFERENCES tenant_c2b84f0e.produtos_base(id);


--
-- Name: pacotes pacotes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.pacotes
    ADD CONSTRAINT pacotes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: pacotes pacotes_ordem_producao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.pacotes
    ADD CONSTRAINT pacotes_ordem_producao_id_fkey FOREIGN KEY (ordem_producao_id) REFERENCES tenant_c2b84f0e.ordens_producao(id);


--
-- Name: pacotes pacotes_produto_sku_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.pacotes
    ADD CONSTRAINT pacotes_produto_sku_id_fkey FOREIGN KEY (produto_sku_id) REFERENCES tenant_c2b84f0e.produtos_skus(id);


--
-- Name: produtos_base produtos_base_categoria_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.produtos_base
    ADD CONSTRAINT produtos_base_categoria_id_fkey FOREIGN KEY (categoria_id) REFERENCES tenant_c2b84f0e.categorias(id);


--
-- Name: produtos_base produtos_base_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.produtos_base
    ADD CONSTRAINT produtos_base_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: produtos_skus produtos_skus_cor_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.produtos_skus
    ADD CONSTRAINT produtos_skus_cor_id_fkey FOREIGN KEY (cor_id) REFERENCES tenant_c2b84f0e.cores(id);


--
-- Name: produtos_skus produtos_skus_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.produtos_skus
    ADD CONSTRAINT produtos_skus_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: produtos_skus produtos_skus_produto_base_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.produtos_skus
    ADD CONSTRAINT produtos_skus_produto_base_id_fkey FOREIGN KEY (produto_base_id) REFERENCES tenant_c2b84f0e.produtos_base(id);


--
-- Name: produtos_skus produtos_skus_tamanho_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.produtos_skus
    ADD CONSTRAINT produtos_skus_tamanho_id_fkey FOREIGN KEY (tamanho_id) REFERENCES tenant_c2b84f0e.tamanhos(id);


--
-- Name: tabela_tempo_padrao tabela_tempo_padrao_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.tabela_tempo_padrao
    ADD CONSTRAINT tabela_tempo_padrao_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: titulos_pagar titulos_pagar_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.titulos_pagar
    ADD CONSTRAINT titulos_pagar_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: titulos_pagar titulos_pagar_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.titulos_pagar
    ADD CONSTRAINT titulos_pagar_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_c2b84f0e.funcionarios(id);


--
-- Name: titulos_receber titulos_receber_cliente_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.titulos_receber
    ADD CONSTRAINT titulos_receber_cliente_id_fkey FOREIGN KEY (cliente_id) REFERENCES tenant_c2b84f0e.clientes(id);


--
-- Name: titulos_receber titulos_receber_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.titulos_receber
    ADD CONSTRAINT titulos_receber_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: unidades_medida unidades_medida_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.unidades_medida
    ADD CONSTRAINT unidades_medida_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: usuario_empresas usuario_empresas_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_c2b84f0e; Owner: -
--

ALTER TABLE ONLY tenant_c2b84f0e.usuario_empresas
    ADD CONSTRAINT usuario_empresas_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_c2b84f0e.empresas(id);


--
-- Name: apontamentos apontamentos_cupom_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.apontamentos
    ADD CONSTRAINT apontamentos_cupom_id_fkey FOREIGN KEY (cupom_id) REFERENCES tenant_df0f12fb.cupons(id);


--
-- Name: apontamentos apontamentos_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.apontamentos
    ADD CONSTRAINT apontamentos_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: apontamentos apontamentos_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.apontamentos
    ADD CONSTRAINT apontamentos_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_df0f12fb.funcionarios(id);


--
-- Name: apontamentos_manuais apontamentos_manuais_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: apontamentos_manuais apontamentos_manuais_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.apontamentos_manuais
    ADD CONSTRAINT apontamentos_manuais_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_df0f12fb.funcionarios(id);


--
-- Name: categorias categorias_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.categorias
    ADD CONSTRAINT categorias_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: clientes clientes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.clientes
    ADD CONSTRAINT clientes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: cupons cupons_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.cupons
    ADD CONSTRAINT cupons_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: cupons cupons_operacao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.cupons
    ADD CONSTRAINT cupons_operacao_id_fkey FOREIGN KEY (operacao_id) REFERENCES tenant_df0f12fb.fichas_tecnicas_operacoes(id);


--
-- Name: cupons cupons_pacote_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.cupons
    ADD CONSTRAINT cupons_pacote_id_fkey FOREIGN KEY (pacote_id) REFERENCES tenant_df0f12fb.pacotes(id);


--
-- Name: departamentos departamentos_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.departamentos
    ADD CONSTRAINT departamentos_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: estoque_movimentacoes estoque_movimentacoes_material_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.estoque_movimentacoes
    ADD CONSTRAINT estoque_movimentacoes_material_id_fkey FOREIGN KEY (material_id) REFERENCES tenant_df0f12fb.materiais(id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: estoque_produtos_movimentacoes estoque_produtos_movimentacoes_produto_sku_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.estoque_produtos_movimentacoes
    ADD CONSTRAINT estoque_produtos_movimentacoes_produto_sku_id_fkey FOREIGN KEY (produto_sku_id) REFERENCES tenant_df0f12fb.produtos_skus(id);


--
-- Name: fichas_tecnicas fichas_tecnicas_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_ficha_tecnica_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_ficha_tecnica_id_fkey FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_df0f12fb.fichas_tecnicas(id);


--
-- Name: fichas_tecnicas_materiais fichas_tecnicas_materiais_material_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.fichas_tecnicas_materiais
    ADD CONSTRAINT fichas_tecnicas_materiais_material_id_fkey FOREIGN KEY (material_id) REFERENCES tenant_df0f12fb.materiais(id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: fichas_tecnicas_operacoes fichas_tecnicas_operacoes_ficha_tecnica_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.fichas_tecnicas_operacoes
    ADD CONSTRAINT fichas_tecnicas_operacoes_ficha_tecnica_id_fkey FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_df0f12fb.fichas_tecnicas(id);


--
-- Name: fichas_tecnicas fichas_tecnicas_produto_base_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.fichas_tecnicas
    ADD CONSTRAINT fichas_tecnicas_produto_base_id_fkey FOREIGN KEY (produto_base_id) REFERENCES tenant_df0f12fb.produtos_base(id);


--
-- Name: cores fk_cores_empresa; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.cores
    ADD CONSTRAINT fk_cores_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: funcionarios fk_funcionarios_grupo; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.funcionarios
    ADD CONSTRAINT fk_funcionarios_grupo FOREIGN KEY (grupo_id) REFERENCES tenant_df0f12fb.grupos_funcionarios(id);


--
-- Name: funcionario_jornadas fk_jornada_funcionario; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.funcionario_jornadas
    ADD CONSTRAINT fk_jornada_funcionario FOREIGN KEY (funcionario_id) REFERENCES tenant_df0f12fb.funcionarios(id) ON DELETE CASCADE;


--
-- Name: materiais_fornecedor fk_matforn_empresa; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.materiais_fornecedor
    ADD CONSTRAINT fk_matforn_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: materiais_fornecedor fk_matforn_fornecedor; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.materiais_fornecedor
    ADD CONSTRAINT fk_matforn_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tenant_df0f12fb.fornecedores(id);


--
-- Name: materiais_fornecedor fk_matforn_material; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.materiais_fornecedor
    ADD CONSTRAINT fk_matforn_material FOREIGN KEY (material_id) REFERENCES tenant_df0f12fb.materiais(id);


--
-- Name: ordens_compra fk_oc_empresa; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_compra
    ADD CONSTRAINT fk_oc_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: ordens_compra fk_oc_fornecedor; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_compra
    ADD CONSTRAINT fk_oc_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tenant_df0f12fb.fornecedores(id);


--
-- Name: ordens_compra_itens fk_oci_material; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_compra_itens
    ADD CONSTRAINT fk_oci_material FOREIGN KEY (material_id) REFERENCES tenant_df0f12fb.materiais(id);


--
-- Name: ordens_compra_itens fk_oci_oc; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_compra_itens
    ADD CONSTRAINT fk_oci_oc FOREIGN KEY (ordem_compra_id) REFERENCES tenant_df0f12fb.ordens_compra(id) ON DELETE CASCADE;


--
-- Name: recebimentos_nf fk_rnf_empresa; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.recebimentos_nf
    ADD CONSTRAINT fk_rnf_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: recebimentos_nf fk_rnf_fornecedor; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.recebimentos_nf
    ADD CONSTRAINT fk_rnf_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES tenant_df0f12fb.fornecedores(id);


--
-- Name: recebimentos_nf fk_rnf_oc; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.recebimentos_nf
    ADD CONSTRAINT fk_rnf_oc FOREIGN KEY (ordem_compra_id) REFERENCES tenant_df0f12fb.ordens_compra(id);


--
-- Name: recebimentos_nf_itens fk_rnfi_material; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.recebimentos_nf_itens
    ADD CONSTRAINT fk_rnfi_material FOREIGN KEY (material_id) REFERENCES tenant_df0f12fb.materiais(id);


--
-- Name: recebimentos_nf_itens fk_rnfi_rnf; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.recebimentos_nf_itens
    ADD CONSTRAINT fk_rnfi_rnf FOREIGN KEY (recebimento_nf_id) REFERENCES tenant_df0f12fb.recebimentos_nf(id) ON DELETE CASCADE;


--
-- Name: tamanhos fk_tamanhos_empresa; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.tamanhos
    ADD CONSTRAINT fk_tamanhos_empresa FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: fornecedores fornecedores_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.fornecedores
    ADD CONSTRAINT fornecedores_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: funcionarios funcionarios_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.funcionarios
    ADD CONSTRAINT funcionarios_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: localizacoes localizacoes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.localizacoes
    ADD CONSTRAINT localizacoes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES master.empresas(id);


--
-- Name: materiais materiais_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.materiais
    ADD CONSTRAINT materiais_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: materiais materiais_fornecedor_padrao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.materiais
    ADD CONSTRAINT materiais_fornecedor_padrao_id_fkey FOREIGN KEY (fornecedor_padrao_id) REFERENCES tenant_df0f12fb.fornecedores(id);


--
-- Name: materiais materiais_unidade_medida_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.materiais
    ADD CONSTRAINT materiais_unidade_medida_id_fkey FOREIGN KEY (unidade_medida_id) REFERENCES tenant_df0f12fb.unidades_medida(id);


--
-- Name: ocorrencias ocorrencias_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ocorrencias
    ADD CONSTRAINT ocorrencias_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: ocorrencias ocorrencias_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ocorrencias
    ADD CONSTRAINT ocorrencias_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_df0f12fb.funcionarios(id);


--
-- Name: ordens_producao ordens_producao_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_producao
    ADD CONSTRAINT ordens_producao_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: ordens_producao ordens_producao_ficha_tecnica_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_producao
    ADD CONSTRAINT ordens_producao_ficha_tecnica_id_fkey FOREIGN KEY (ficha_tecnica_id) REFERENCES tenant_df0f12fb.fichas_tecnicas(id);


--
-- Name: ordens_producao_itens ordens_producao_itens_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: ordens_producao_itens ordens_producao_itens_ordem_producao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_ordem_producao_id_fkey FOREIGN KEY (ordem_producao_id) REFERENCES tenant_df0f12fb.ordens_producao(id);


--
-- Name: ordens_producao_itens ordens_producao_itens_produto_sku_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_producao_itens
    ADD CONSTRAINT ordens_producao_itens_produto_sku_id_fkey FOREIGN KEY (produto_sku_id) REFERENCES tenant_df0f12fb.produtos_skus(id);


--
-- Name: ordens_producao ordens_producao_produto_base_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.ordens_producao
    ADD CONSTRAINT ordens_producao_produto_base_id_fkey FOREIGN KEY (produto_base_id) REFERENCES tenant_df0f12fb.produtos_base(id);


--
-- Name: pacotes pacotes_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.pacotes
    ADD CONSTRAINT pacotes_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: pacotes pacotes_ordem_producao_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.pacotes
    ADD CONSTRAINT pacotes_ordem_producao_id_fkey FOREIGN KEY (ordem_producao_id) REFERENCES tenant_df0f12fb.ordens_producao(id);


--
-- Name: pacotes pacotes_produto_sku_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.pacotes
    ADD CONSTRAINT pacotes_produto_sku_id_fkey FOREIGN KEY (produto_sku_id) REFERENCES tenant_df0f12fb.produtos_skus(id);


--
-- Name: produtos_base produtos_base_categoria_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.produtos_base
    ADD CONSTRAINT produtos_base_categoria_id_fkey FOREIGN KEY (categoria_id) REFERENCES tenant_df0f12fb.categorias(id);


--
-- Name: produtos_base produtos_base_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.produtos_base
    ADD CONSTRAINT produtos_base_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: produtos_skus produtos_skus_cor_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.produtos_skus
    ADD CONSTRAINT produtos_skus_cor_id_fkey FOREIGN KEY (cor_id) REFERENCES tenant_df0f12fb.cores(id);


--
-- Name: produtos_skus produtos_skus_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.produtos_skus
    ADD CONSTRAINT produtos_skus_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: produtos_skus produtos_skus_produto_base_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.produtos_skus
    ADD CONSTRAINT produtos_skus_produto_base_id_fkey FOREIGN KEY (produto_base_id) REFERENCES tenant_df0f12fb.produtos_base(id);


--
-- Name: produtos_skus produtos_skus_tamanho_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.produtos_skus
    ADD CONSTRAINT produtos_skus_tamanho_id_fkey FOREIGN KEY (tamanho_id) REFERENCES tenant_df0f12fb.tamanhos(id);


--
-- Name: tabela_tempo_padrao tabela_tempo_padrao_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.tabela_tempo_padrao
    ADD CONSTRAINT tabela_tempo_padrao_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: titulos_pagar titulos_pagar_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.titulos_pagar
    ADD CONSTRAINT titulos_pagar_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: titulos_pagar titulos_pagar_funcionario_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.titulos_pagar
    ADD CONSTRAINT titulos_pagar_funcionario_id_fkey FOREIGN KEY (funcionario_id) REFERENCES tenant_df0f12fb.funcionarios(id);


--
-- Name: titulos_receber titulos_receber_cliente_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.titulos_receber
    ADD CONSTRAINT titulos_receber_cliente_id_fkey FOREIGN KEY (cliente_id) REFERENCES tenant_df0f12fb.clientes(id);


--
-- Name: titulos_receber titulos_receber_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.titulos_receber
    ADD CONSTRAINT titulos_receber_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: unidades_medida unidades_medida_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.unidades_medida
    ADD CONSTRAINT unidades_medida_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- Name: usuario_empresas usuario_empresas_empresa_id_fkey; Type: FK CONSTRAINT; Schema: tenant_df0f12fb; Owner: -
--

ALTER TABLE ONLY tenant_df0f12fb.usuario_empresas
    ADD CONSTRAINT usuario_empresas_empresa_id_fkey FOREIGN KEY (empresa_id) REFERENCES tenant_df0f12fb.empresas(id);


--
-- PostgreSQL database dump complete
--

\unrestrict 3OSl06fVJhviFLfSd0namp54kEC1m8C1vF9junuDv6y2BRskOmsNIYAKY9E0tbe

