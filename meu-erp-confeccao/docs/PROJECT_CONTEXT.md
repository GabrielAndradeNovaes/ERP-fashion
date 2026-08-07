# Contexto do Projeto: ERP Confecção (SaaS)

## 1. Visão de Negócio
O sistema será um ERP comercial (SaaS) vendido em massa para pequenas e médias indústrias de confecção. O maior desafio técnico é lidar com a matriz multidimensional de produtos (grades de tamanhos e cores) e o fluxo descentralizado de produção (facções terceirizadas).

## 2. Arquitetura Multi-Tenant
Para garantir escalabilidade, segurança e suporte a planos Enterprise, adotaremos o padrão **Database-per-Tenant** com um Banco Master de roteamento.
- **Control Plane (Banco Master):** Guarda os cadastros das empresas contratantes (`clientes_tenant`), usuários de acesso e as credenciais (`instancias_db`) apontando para onde estão os dados físicos de cada cliente.
- **Tenant DB (Banco do Cliente):** Cada cliente possui seu próprio banco de dados relacional isolado (ou schema), contendo suas tabelas de produtos, estoque e ordens de produção.

## 3. Stack Tecnológica
- **Backend:** Java 17+ com Spring Boot 3. (Escolhido pela robustez corporativa, tipagem estática e suporte nativo a transações complexas e roteamento multi-tenant).
- **Banco de Dados:** PostgreSQL (Tanto para o Master quanto para os Tenants).
- **Migrations:** Flyway (com lógica customizada para rodar migrações dinamicamente em todos os tenants).
- **Frontend:** React com Vite, TypeScript e tipagem estrita, organizado de forma modular. Utiliza **Material UI (MUI)** para a camada visual de componentes e **TanStack Table** para renderização e controle lógico de listagens e grades complexas de dados.
- **Cache/Fila:** Redis (para processamento assíncrono e gerenciamento de tokens/sessões).
- **Infraestrutura Local:** Docker e Docker Compose.

## 4. Modelagem Core (O Domínio Têxtil)
A estrutura de dados precisa refletir a complexidade do chão de fábrica:
- **Catálogo Base:** Tabelas para `produtos_base` (o modelo genérico), `atributos_cores` e `atributos_tamanhos`.
- **SKUs (Grade):** Tabela `produtos_skus` que materializa o cruzamento do produto base com cor e tamanho (ex: Camiseta Gola V - Azul - M).
- **Ficha Técnica (BOM):** Tabelas `fichas_tecnicas` (cabeçalho e versão) e `fichas_tecnicas_materiais` (ingredientes). O consumo de material varia dependendo do tamanho e cor da peça que será produzida.
- **PCP e Estoque:** Movimentação transacional rigorosa para baixar tecido em rolos e gerar ordens de corte e costura (interna ou via Facções).

## 5. Regras de Desenvolvimento
- A API será "API-First" (RESTful), visando integrações fáceis com e-commerces (Nuvemshop) e emissores fiscais (Bling).
- Segurança rigorosa com JWT. O Token sempre conterá o `tenant_id` para roteamento no Spring Boot.
- Manter acoplamento fraco entre os módulos lógicos do sistema.
- **ADRs Obrigatórias:** Toda decisão técnica, nova feature, modelagem de BD ou alteração de arquitetura será documentada em `docs/ADR/` antes do desenvolvimento.
