ALTER TABLE fornecedores 
ADD COLUMN IF NOT EXISTS avaliacao INT DEFAULT 5;

CREATE TABLE IF NOT EXISTS ordens_compra (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL,
    fornecedor_id UUID NOT NULL,
    numero_pedido VARCHAR(50) NOT NULL,
    data_emissao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_previsao_entrega DATE,
    status VARCHAR(50) DEFAULT 'RASCUNHO' NOT NULL, -- RASCUNHO, EMITIDA, PARCIALMENTE_RECEBIDA, RECEBIDA, CANCELADA
    valor_total DECIMAL(12, 2) DEFAULT 0,
    observacoes TEXT,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_oc_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id),
    CONSTRAINT fk_oc_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedores(id)
);

CREATE TABLE IF NOT EXISTS ordens_compra_itens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ordem_compra_id UUID NOT NULL,
    material_id UUID NOT NULL,
    quantidade_solicitada DECIMAL(12, 3) NOT NULL,
    quantidade_recebida DECIMAL(12, 3) DEFAULT 0,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    valor_total DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_oci_oc FOREIGN KEY (ordem_compra_id) REFERENCES ordens_compra(id) ON DELETE CASCADE,
    CONSTRAINT fk_oci_material FOREIGN KEY (material_id) REFERENCES materiais(id)
);

CREATE TABLE IF NOT EXISTS recebimentos_nf (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    empresa_id UUID NOT NULL,
    fornecedor_id UUID,
    ordem_compra_id UUID,
    chave_acesso_nfe VARCHAR(100),
    numero_nfe VARCHAR(50),
    data_emissao TIMESTAMP,
    data_recebimento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    xml_conteudo TEXT,
    valor_total_nf DECIMAL(12, 2) DEFAULT 0,
    status VARCHAR(50) DEFAULT 'PENDENTE' NOT NULL, -- PENDENTE, PROCESSADA
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rnf_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id),
    CONSTRAINT fk_rnf_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedores(id),
    CONSTRAINT fk_rnf_oc FOREIGN KEY (ordem_compra_id) REFERENCES ordens_compra(id)
);

CREATE TABLE IF NOT EXISTS recebimentos_nf_itens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recebimento_nf_id UUID NOT NULL,
    material_id UUID, -- Pode ser null se ainda não foi feito o de/para
    nome_produto_nf VARCHAR(255),
    codigo_produto_nf VARCHAR(100),
    ncm_nf VARCHAR(20),
    quantidade DECIMAL(12, 3) NOT NULL,
    valor_unitario DECIMAL(10, 2) NOT NULL,
    valor_total DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_rnfi_rnf FOREIGN KEY (recebimento_nf_id) REFERENCES recebimentos_nf(id) ON DELETE CASCADE,
    CONSTRAINT fk_rnfi_material FOREIGN KEY (material_id) REFERENCES materiais(id)
);
