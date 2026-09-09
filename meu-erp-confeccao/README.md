# Fashion ERP (Meu ERP Confecção)

Sistema ERP SaaS especializado para a indústria de confecção têxtil e vestuário.

O objetivo do sistema é fornecer controle total desde o cadastro da Ficha Técnica (BOM), passando pela criação de Ordens de Produção (PCP), controle de estoque, geração de pacotes com código de barras, até a bipagem e controle de produtividade e rendimento de funcionários.

## Arquitetura e Tecnologias

- **Backend:** Java 17, Spring Boot 3, Spring Security (JWT)
- **Frontend:** React, Vite, TypeScript, Material-UI (MUI)
- **Banco de Dados:** PostgreSQL 15 (com arquitetura SaaS Schema-per-Tenant)
- **Cache/Background:** Redis
- **Infraestrutura:** Docker e Docker Compose

## Estrutura do Projeto

- **`frontend/`**: Aplicação SPA React.
- **`backend/`**: API RESTful do backend.
- **`infra/`**: Scripts e configurações do Docker.
- **`docs/`**: Central de Documentação do Projeto (Arquitetura, Contexto, Decisões Técnicas).

## Documentação Técnica

Recomendamos fortemente a leitura da documentação oficial do projeto antes de iniciar o desenvolvimento:

- [Contexto do Projeto (Visão de Negócio e Arquitetura)](docs/PROJECT_CONTEXT.md)
- [Documentação Técnica (Dicionário de Dados e Endpoints)](docs/DOCUMENTACAO_TECNICA.md)
- [Padrões de Execução (Boas Práticas)](docs/EXECUCAO.md)

---
*Nota: A documentação do projeto é atualizada continuamente a cada nova funcionalidade adicionada.*
