# TCC: Resumo e Estrutura do Artigo (Monografia)

Este documento contém a estrutura base e os textos de apoio para a escrita do Trabalho de Conclusão de Curso (TCC) focado no Fashion ERP.

---

## 1. Introdução e Contextualização
A indústria de confecção de vestuário é um dos setores mais tradicionais e relevantes da economia, sendo caracterizada por uma cadeia produtiva fragmentada e intensiva em mão de obra. Pequenas e médias empresas (PMEs) desse setor enfrentam desafios diários em relação ao controle de suprimentos, gestão de engenharia de produto (fichas técnicas) e coordenação do chão de fábrica e das oficinas terceirizadas (facções). 
O Fashion ERP surge como uma plataforma SaaS (Software as a Service) desenvolvida para digitalizar e otimizar a gestão das PMEs de confecção, oferecendo ferramentas de grande porte a um custo acessível mediante infraestrutura em nuvem compartilhada.

## 2. Problema de Pesquisa
"Como a adoção de um sistema ERP com arquitetura SaaS Multi-Tenant pode solucionar os gargalos de apontamento de produtividade e controle de estoque dimensional (grades de cor e tamanho) em pequenas e médias indústrias de confecção?"

## 3. Justificativa
A maioria dos softwares de gestão disponíveis no mercado são generalistas. Eles não lidam bem com a matriz dimensional do setor têxtil, onde uma única camiseta gera múltiplos SKUs devido à combinação de cores e tamanhos. Além disso, a dificuldade em calcular o custo de produção exato (Standard Allowed Minute - SAM) e controlar as peças que vão e voltam de oficinas terceirizadas frequentemente resulta em desperdício de insumos e atrasos na entrega. O projeto justifica-se por trazer uma modelagem de domínio altamente especialista para a indústria do vestuário, unida a uma arquitetura de software moderna.

## 4. Objetivos
**Objetivo Geral:** 
Desenvolver uma plataforma ERP na nuvem (SaaS) especializada na gestão da produção têxtil, com foco em rastreabilidade, controle dimensional e gestão de produtividade.

**Objetivos Específicos:**
- Implementar uma arquitetura de banco de dados *Schema-per-Tenant* capaz de isolar os dados de diferentes clientes mantendo a mesma base de código.
- Desenvolver um módulo de Ficha Técnica (BOM - Bill of Materials) que calcule o consumo de insumos e os tempos operacionais de costura.
- Criar rotinas de PCP (Planejamento e Controle da Produção) com geração e bipagem de cupons com códigos de barra para apontamento de produtividade das costureiras.
- Assegurar a segurança e performance da aplicação por meio de autenticação JWT, isolamento de requisições e rate limiting.

## 5. Referencial Teórico
Tópicos sugeridos para abordar na escrita acadêmica:
1. **Sistemas de Informação na Manufatura Têxtil:** A importância do PCP e da Ficha Técnica.
2. **Cloud Computing e SaaS:** Modelos de distribuição de software e vantagens para PMEs.
3. **Arquitetura Multi-Tenant:** Padrões de isolamento de dados (*Database-per-Tenant*, *Schema-per-Tenant*, *Row-level Isolation*).
4. **Tecnologias Modernas de Desenvolvimento:** O ecossistema Spring Boot, React, e conteinerização com Docker.

## 6. Metodologia e Tecnologias Adotadas
O desenvolvimento seguiu princípios de metodologias ágeis (sprints curtas) e a arquitetura "API-First".
- **Backend:** Java 17, Spring Boot 3, Hibernate/JPA.
- **Banco de Dados:** PostgreSQL 15, escolhido pelo excelente suporte nativo a manipulação de esquemas múltiplos.
- **Controle de Versão do Banco:** Flyway configurado para orquestração dinâmica de migrations em múltiplos esquemas simultaneamente.
- **Frontend:** React, Vite e TypeScript, utilizando a biblioteca Material UI (MUI) para garantir um design system moderno, fluido e de fácil manutenção.
- **Infraestrutura:** Docker e Docker Compose, possibilitando a criação de containers replicáveis para Banco de Dados, Aplicação e Cache (Redis).

## 7. Resultados Alcançados
- Criação de um ecossistema seguro onde um único servidor hospeda múltiplos clientes isolados logicamente.
- Gestão centralizada da engenharia do produto, resolvendo o problema dos SKUs de grade.
- Operação otimizada no chão de fábrica através da emissão de pacotes e cupons scaneáveis.
- Autenticação e roteamento de tráfego que identifica de qual "empresa" é o usuário ativo antes da execução de qualquer query SQL no banco de dados.

## 8. Trabalhos Futuros
- Módulo Integrador Fiscal: Geração nativa de NF-e e integração com SEFAZ.
- Controle físico e logístico apurado de facções terceirizadas via aplicativo móvel.
- Análise de Dados (Dashboards e IA) para predição de demanda e otimização de compra de fios e tecidos.
