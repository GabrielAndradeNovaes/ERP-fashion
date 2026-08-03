# Documentação Técnica do Sistema (ERP Confecção)

Este documento detalha a arquitetura técnica, as tabelas, campos e regras de negócio implementadas até o momento no projeto.

## 1. Visão Geral da Arquitetura
O sistema segue o modelo **SaaS Multi-Tenant** utilizando a abordagem **Database-per-Tenant**.
- **Backend:** Java 17 com Spring Boot 3
- **Frontend:** React + Vite + TypeScript (a ser implementado/expandido)
- **Banco de Dados:** PostgreSQL 15 (Isolamento lógico/físico de clientes)
- **Cache / Fila:** Redis 7

A interceptação de requisições (`TenantInterceptor`) lê o cabeçalho `X-TenantID` para realizar o roteamento dinâmico da conexão do banco de dados na camada do Hibernate/JPA.

---

## 2. Dicionário de Dados e Tabelas (Módulos Core)

Abaixo estão os detalhes das tabelas relacionais do sistema. O Spring Data JPA é responsável por gerar ou mapear essas tabelas no banco de dados de cada Tenant (Cliente).

### 2.1. Módulo: Catálogo de Produtos

#### Tabela: `produtos_base`
Representa o modelo genérico de um produto antes de suas variações (SKUs).
- `id` (UUID, PK): Identificador único.
- `codigo` (Varchar 50, Único): Código de referência interno (Ex: CAM-001).
- `nome` (Varchar 255): Nome comercial do produto.
- `descricao` (Text): Detalhamento opcional do produto.
- `criado_em` (Timestamp): Data de cadastro do produto.

#### Tabela: `produtos_skus`
Materializa a grade (cruzamento de tamanhos e cores).
- `id` (UUID, PK): Identificador único do SKU.
- `produto_base_id` (UUID, FK): Vínculo com a tabela `produtos_base`.
- `cor` (Varchar 50): Cor do produto (Ex: Azul).
- `tamanho` (Varchar 10): Tamanho do produto (Ex: P, M, G, GG).
- `codigo_barras` (Varchar 100, Único): Código EAN/GTIN.
- `preco_venda` (Decimal 19,4): Preço final do item.

---

### 2.2. Módulo: Estoque (Inventory)

#### Tabela: `materiais`
Cadastro de insumos e matérias-primas utilizadas na confecção.
- `id` (UUID, PK): Identificador único.
- `codigo` (Varchar 50, Único): Código do material (Ex: TEC-001).
- `nome` (Varchar 255): Nome do material (Ex: Tecido Algodão).
- `descricao` (Text): Detalhamento do material.
- `unidade_medida` (Varchar 20): Unidade de controle (Ex: KG, METRO, UNIDADE).
- `criado_em` (Timestamp): Data de cadastro.

#### Tabela: `estoque_movimentacoes`
Kardex de entradas e saídas de materiais.
- `id` (UUID, PK): Identificador único da movimentação.
- `material_id` (UUID, FK): Vínculo com a tabela `materiais`.
- `tipo` (Varchar 20): Tipo de movimento (ENTRADA ou SAIDA).
- `quantidade` (Decimal 19,4): Quantidade movimentada.
- `documento_referencia` (Varchar 100): Nota fiscal ou ordem de corte atrelada.
- `data_movimentacao` (Timestamp): Data e hora da ocorrência.

---

### 2.3. Módulo: Produção e PCP (Ficha Técnica)

#### Tabela: `fichas_tecnicas`
Cabeçalho do Bill of Materials (BOM). Define "como" o produto é feito.
- `id` (UUID, PK): Identificador único da ficha.
- `produto_base_id` (UUID, FK): Vínculo com a tabela `produtos_base`.
- `versao` (Varchar 10): Versão da ficha (Ex: v1, v2).
- `observacoes` (Text): Instruções de corte ou costura.
- `criado_em` (Timestamp): Data de criação da ficha.

#### Tabela: `fichas_tecnicas_materiais`
Itens (ingredientes) da Ficha Técnica.
- `id` (UUID, PK): Identificador único.
- `ficha_tecnica_id` (UUID, FK): Vínculo com a tabela `fichas_tecnicas`.
- `material_id` (UUID, FK): Vínculo com o insumo (`materiais`).
- `quantidade` (Decimal 10,4): Quantidade do material consumida por unidade produzida.

---

## 3. Integração e Endpoints da API

Todos os endpoints requerem o header `X-TenantID`.

| Módulo | Endpoint | Métodos Suportados | Descrição |
|--------|----------|--------------------|-----------|
| Catálogo | `/api/catalog/produtos` | GET, POST | Gerencia produtos e seus SKUs. |
| Estoque | `/api/inventory/materiais` | GET, POST | Gerencia cadastro de matérias-primas. |
| Estoque | `/api/inventory/materiais/{id}` | GET | Busca um material específico. |
| Produção | `/api/production/fichas-tecnicas` | POST | Cria Fichas Técnicas atreladas a produtos e materiais. |
| Produção | `/api/production/fichas-tecnicas/produto/{id}` | GET | Busca fichas técnicas de um produto. |

---

## 4. Próximos Passos (Roadmap Técnico)

1. **Ordens de Produção (PCP):** Implementar o módulo que une a Ficha Técnica ao Estoque, gerando OPs e dando baixa automática nos materiais via `estoque_movimentacoes`.
2. **Integração Frontend:** Conectar as telas do React (Vite) aos módulos já criados.
3. **Autenticação Real:** Trocar o `TenantInterceptor` por uma validação JWT real onde o token dita o `TenantID`.
