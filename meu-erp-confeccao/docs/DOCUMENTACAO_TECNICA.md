# Documentação Técnica do Sistema (ERP Confecção)

Este documento detalha a arquitetura técnica, as tabelas, campos e regras de negócio implementadas até o momento no projeto.

## 1. Visão Geral da Arquitetura
O sistema segue o modelo **SaaS Multi-Tenant** utilizando a abordagem **Schema-per-Tenant**.
- **Backend:** Java 17 com Spring Boot 3
- **Frontend:** React + Vite + TypeScript
- **Banco de Dados:** PostgreSQL 15 (Isolamento lógico de clientes por schemas, ex: `tenant_1`, `tenant_c2b84f0e`)
- **Autenticação:** JWT com extração dinâmica do `tenantId` e escopos de permissões.

A interceptação de requisições e a resolução do schema ativo é feita de forma transparente pela integração do JWT com a camada do Hibernate/JPA, eliminando a necessidade de repasse manual de cabeçalhos nas requisições do front-end.

---

## 2. Dicionário de Dados e Tabelas

Abaixo estão os detalhes das tabelas relacionais do sistema, divididas entre o Schema Master e os Schemas de Tenants.

### 2.0. Módulo: SaaS Core (Schema `master`)

#### Tabela: `clientes_tenant`
Representa as empresas que contrataram o ERP.
- `id` (UUID, PK): Identificador único do cliente.
- `slug` (Varchar, Único): Identificador na URL/banco (Ex: `petrobras-edise`).
- `schema_name` (Varchar): Nome do schema no Postgres (Ex: `tenant_1`).
- `status` (Enum): Ativo, inativo, pendente.

#### Tabela: `usuarios`
Central de autenticação e contas de acesso.
- `id` (UUID, PK): Identificador do usuário.
- `email` (Varchar, Único): E-mail de login.
- `senha` (Varchar): Senha criptografada.
- `tenant_id` (UUID, FK): Vínculo com qual `clientes_tenant` este usuário pertence.
- `role` (Varchar): Papel no sistema (Ex: ADMIN, USER).

---

### 2.1. Módulo: Catálogo de Produtos (Schema Tenant)

#### Tabela: `produtos_base`
Representa o modelo genérico de um produto antes de suas variações (SKUs).
- `id` (UUID, PK): Identificador único.
- `codigo` (Varchar 50, Único): Código de referência interno (Ex: CAM-001).
- `nome` (Varchar 255): Nome comercial do produto.
- `descricao` (Text): Detalhamento opcional do produto.

#### Tabela: `produtos_skus`
Materializa a grade (cruzamento de tamanhos e cores).
- `id` (UUID, PK): Identificador único do SKU.
- `produto_base_id` (UUID, FK): Vínculo com `produtos_base`.
- `cor` (Varchar 50): Cor do produto.
- `tamanho` (Varchar 10): Tamanho do produto.
- `codigo_barras` (Varchar 100, Único): Código EAN/GTIN.
- `preco_venda` (Decimal 19,4): Preço final do item.

---

### 2.2. Módulo: Estoque (Inventory) (Schema Tenant)

#### Tabela: `materiais`
Cadastro de insumos e matérias-primas utilizadas na confecção.
- `id` (UUID, PK): Identificador único.
- `codigo` (Varchar 50, Único): Código do material.
- `nome` (Varchar 255): Nome do material.

#### Tabela: `estoque_movimentacoes`
Kardex de entradas e saídas de materiais.
- `id` (UUID, PK): Identificador da movimentação.
- `material_id` (UUID, FK): Vínculo com `materiais`.
- `tipo` (Varchar 20): Tipo de movimento (ENTRADA ou SAIDA).
- `quantidade` (Decimal 19,4): Quantidade movimentada.

---

### 2.3. Módulo: Engenharia (Ficha Técnica) (Schema Tenant)

#### Tabela: `fichas_tecnicas`
Cabeçalho do Bill of Materials (BOM). Define "como" o produto é feito.
- `id` (UUID, PK): Identificador único da ficha.
- `produto_base_id` (UUID, FK): Vínculo com `produtos_base`.
- `versao` (Varchar 10): Versão da ficha.

#### Tabela: `fichas_tecnicas_materiais`
Itens (ingredientes) da Ficha Técnica.
- `id` (UUID, PK): Identificador único.
- `ficha_tecnica_id` (UUID, FK): Vínculo com a `fichas_tecnicas`.
- `material_id` (UUID, FK): Vínculo com o insumo (`materiais`).
- `quantidade` (Decimal 10,4): Quantidade do material consumida.

#### Tabela: `fichas_tecnicas_operacoes`
Roteiro de costura e operações. Agora suporta cálculo exato do Tempo Padrão (SAM) via parâmetros físicos da operação.
- `id` (UUID, PK): Identificador único.
- `ficha_tecnica_id` (UUID, FK): Vínculo com a `fichas_tecnicas`.
- `nome` (Varchar): Nome da operação (Ex: Fechar lateral).
- `rpm_maquina` (Integer): Velocidade de costura (RPM) da máquina.
- `pontos_por_cm` (Decimal): Quantidade de pontos por centímetro.
- `comprimento_costura_cm` (Decimal): Distância percorrida na costura em centímetros.
- `tipo_trajeto` (Enum): RETA, CURVA ou CICLO_FIXO.
- `dificuldade_tecido` (Enum): NORMAL, MALHA, ESCORREGADIO, etc.
- `quantidade_folhas` (Integer): Quantidade de partes (folhas) que serão manuseadas.
- `quantidade_paradas` (Integer): Número de paradas (forçadas + calculadas) na operação.
- `sam_minutos` (Decimal): Tempo final calculado para executar esta operação em minutos (Standard Allowed Minute).

---

### 2.4. Módulo: Ordens de Produção (PCP) (Schema Tenant)

#### Tabela: `ordens_producao`
Solicitação de fabricação de um produto.
- `id` (UUID, PK): Identificador da OP.
- `numero` (Varchar): Número da OP.
- `produto_base_id` (UUID, FK): Produto que será produzido.
- `status` (Enum): PENDENTE, EM_ANDAMENTO, CONCLUIDA.

#### Tabela: `pacotes`
Lote físico gerado a partir da OP (Ex: Amarrado de 20 camisetas).
- `id` (UUID, PK): Identificador.
- `ordem_producao_id` (UUID, FK): OP de origem.
- `produto_sku_id` (UUID, FK): Qual SKU específico é este pacote.
- `quantidade_pecas` (Int): Quantas peças compõem o pacote.
- `codigo_barras` (Varchar 50, Único): Código de barras único do pacote.

#### Tabela: `cupons`
Tickets operacionais destacáveis de cada pacote para leitura (bipagem) de produtividade.
- `id` (UUID, PK): Identificador.
- `pacote_id` (UUID, FK): Pacote vinculado.
- `operacao_id` (UUID, FK): Qual operação (`fichas_tecnicas_operacoes`) este cupom representa.
- `codigo_barras` (Varchar 100, Único): Código lido pelo scanner da costureira.
- `tempo_total_centesimal` (Decimal): Tempo ganho ao processar este lote.

---

### 2.5. Módulo: RH e Produtividade (Schema Tenant)

#### Tabela: `funcionarios`
Colaboradores do chão de fábrica (costureiras, cortadores).
- `id` (UUID, PK): Identificador.
- `nome` (Varchar): Nome do colaborador.

#### Tabela: `funcionario_jornadas`
Grade de horários teóricos de trabalho para cálculo de rendimento. Pode conter múltiplas pausas (Ex: 07:00-09:00 e 09:15-12:00).
- `id` (UUID, PK): Identificador.
- `funcionario_id` (UUID, FK): Vínculo ao funcionário.
- `dia_semana` (Int): Dia da semana.
- `entrada` (Time): Hora de entrada.
- `saida` (Time): Hora de saída.

#### Tabela: `apontamentos`
Registro de leitura do cupom (bipagem) ou apontamento de tempo manual pelas costureiras.
- `id` (UUID, PK): Identificador.
- `funcionario_id` (UUID, FK): Quem realizou ou quem recebe o tempo.
- `cupom_id` (UUID, FK, Nullable): Qual cupom foi bipado (se automático).
- `data_hora` (Timestamp): Momento do registro.
- `tempo_ganho` (Decimal): Minutos de produtividade recebidos.
- `tipo` (Enum): AUTOMATICO (via bipe) ou MANUAL.

---

### 2.6. Módulo: Financeiro (Schema Tenant)

#### Tabela: `titulos_receber`
Contas a receber dos clientes.
- `id` (UUID, PK): Identificador do título.
- `descricao` (Varchar): Descrição do título.
- `valor` (Decimal 19,4): Valor do título.
- `data_vencimento` (Date): Data de vencimento.
- `status` (Enum): PENDING, PAID, CANCELED, REFUNDED.

#### Tabela: `transacoes_pagamento`
Registro das transações individuais de um título a receber.
- `id` (UUID, PK): Identificador da transação.
- `titulo_receber_id` (UUID, FK): Vínculo com `titulos_receber`.
- `gateway` (Enum): MERCADOPAGO, MOCK, etc.
- `gateway_transacao_id` (Varchar): Identificador externo.
- `status` (Enum): PENDING, PAID, REJECTED, REFUNDED, EXPIRED.
- `qr_code_payload` (Text): Payload PIX Copia e Cola.

---

## 3. Integração e Endpoints da API

Todos os endpoints requerem autenticação JWT (`Authorization: Bearer <token>`), cujo token inclui a identificação da empresa internamente.

| Módulo | Endpoint Base | Descrição |
|--------|---------------|-----------|
| Autenticação | `/api/auth/login` | Rota pública para obtenção do JWT e Login. |
| Tenants | `/api/admin/tenants` | Cadastro e setup de novos schemas de banco para empresas. |
| Catálogo | `/api/catalog/produtos` | Gerencia produtos e SKUs. |
| Estoque | `/api/inventory/materiais` | Gerencia insumos. |
| Ficha Técnica | `/api/production/fichas-tecnicas` | Configura materiais e roteiro (operações). |
| Produção | `/api/production/ordens` | Criação, listagem e alteração de status de OPs. |
| Pacotes | `/api/production/ordens/{id}/gerar-pacotes` | Gera pacotes e cupons com códigos de barra baseados no tamanho do pacote. |
| Bipagem | `/api/production/apontamentos` | Recebe a leitura do código do cupom ou tempo manual para pontuar o funcionário. |
| RH | `/api/funcionarios` | Cadastro de funcionários e jornadas detalhadas. |
| Produtividade| `/api/production/produtividade` | Geração do relatório de produtividade (Tempo Teórico vs Tempo Real vs Rendimento). |
| Financeiro Admin | `/api/financeiro/receber` | Cadastro e listagem de Títulos a Receber, geração manual de pagamentos PIX. |
| Checkout Público | `/api/public/checkout` | Consulta de detalhes de pagamento (QR Code Pix) e mock webhooks simulados via polling. |

---

## 4. Próximos Passos (Roadmap Técnico)

1. **Dashboard Inicial:** Criar uma visão executiva do status de OPs, pendências de corte e costura.
2. **Gestão de Facções:** Expandir o sistema para despachar OPs fisicamente para empresas terceirizadas (Facções) e controlar o retorno.
3. **Gestão Financeira/Custos:** Calcular o custo final da OP cruzando com custos fixos e variáveis, comparando com o previsto na ficha técnica.
