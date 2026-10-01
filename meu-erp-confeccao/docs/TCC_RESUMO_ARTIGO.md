# TCC: Estrutura do Artigo e Monografia

Este documento detalha a estrutura da monografia baseada no viés de "Transparência nas Relações de Trabalho e Inclusão Digital" aplicado à indústria de confecção têxtil.

---

## 1. Título Provisório
**Transparência e Rastreabilidade no Setor Têxtil: Uma Abordagem SaaS Multi-Tenant para a Inclusão Digital e Formalização do Chão de Fábrica.**

## 2. Tema e Delimitação
O trabalho aborda o desenvolvimento de uma arquitetura de software Multi-Tenant (SaaS) aplicada à gestão de Planejamento e Controle de Produção (PCP) em micro e pequenas facções têxteis. O foco será demonstrar como a digitalização do apontamento de produtividade (via código de barras) resolve problemas crônicos de subnotificação de trabalho e informalidade.

## 3. Problemática (A Pergunta Central)
De que forma o desenvolvimento de um sistema ERP em nuvem (SaaS), com arquitetura Multi-Tenant acessível e rastreabilidade de PCP, pode mitigar a informalidade e promover a transparência nas relações de remuneração em micro e pequenas indústrias têxteis?

## 4. Objetivos

**Objetivo Geral:**
Desenvolver um sistema SaaS Multi-Tenant para o setor têxtil e analisar como sua implementação arquitetural e funcional contribui para a transparência trabalhista e a inclusão digital de pequenas facções.

**Objetivos Específicos:**
- **Técnico:** Projetar e implementar uma arquitetura de banco de dados Multi-Tenant (Schema-per-tenant) com provisionamento automatizado de novos clientes.
- **Técnico:** Desenvolver o módulo de PCP com funcionalidade de "bipagem" (leitura de código de barras) para rastreamento preciso do tempo e eficiência por operação.
- **Social/Analítico:** Demonstrar, através da modelagem do sistema, como a substituição de controles manuais (papel) por registros digitais imutáveis protege os colaboradores contra perdas financeiras (horas não pagas) e protege as empresas contra inconsistências.
- **Social/Analítico:** Avaliar como a redução de barreiras de infraestrutura (via modelo SaaS) democratiza o acesso de microempreendedores locais a tecnologias de gestão de nível corporativo.

## 5. Justificativa

**Justificativa Social:** 
A indústria têxtil periférica (facções) opera majoritariamente na informalidade e é dependente de controles manuais suscetíveis a erros e fraudes. A digitalização do esforço produtivo garante o pagamento justo pelas peças produzidas, protegendo a força de trabalho e empoderando os microempreendedores com dados para tomada de decisão.

**Justificativa Tecnológica:** 
Implementar um SaaS Multi-Tenant exige a resolução de problemas complexos de engenharia de software (isolamento de dados, migrações dinâmicas com Flyway, roteamento em tempo real), validando os conhecimentos avançados adquiridos durante a graduação.

## 6. Metodologia Proposta
- **Natureza da Pesquisa:** Pesquisa Aplicada (pois resultará em um produto de software funcional).
- **Abordagem:** Qualitativa (análise do impacto do software no modelo de negócio e nas relações de trabalho).
- **Procedimentos Técnicos:**
  1. Levantamento de Requisitos (Mapeamento das regras de negócio têxtil).
  2. Desenvolvimento de Software (Utilizando Java/Spring Boot, React/TypeScript e PostgreSQL).
  3. Arquitetura de Banco de Dados Multi-Tenant (Schema-per-Tenant).

## 7. Estrutura de Capítulos (O Esqueleto do Texto)

1. **Introdução:** Contextualização do setor têxtil, apresentação do problema, objetivos e justificativa.
2. **Fundamentação Teórica:**
   - O Setor Têxtil e o modelo de Facções (A dor social e a informalidade).
   - Conceitos de Computação em Nuvem e SaaS (Software as a Service).
   - Arquiteturas Multi-Tenant (Abordagens de isolamento de dados: Coluna vs. Schema vs. Database).
3. **Metodologia e Tecnologias:** Como o sistema foi construído e quais stacks foram utilizadas (Spring Boot, React, Postgres, Docker).
4. **Projeto e Arquitetura do Sistema:**
   - A Orquestração do Control Plane (Banco Master e Provisionamento Automatizado).
   - Roteamento Dinâmico de Banco de Dados (JWT e Filters).
   - Modelagem do Domínio Têxtil (Ficha Técnica, Pacotes e Cupons).
5. **O Módulo de PCP e o Impacto Social (Resultados e Discussões):**
   - A lógica da Bipagem de Cupons e cálculo de produtividade real vs. teórica.
   - Análise de como essa ferramenta substitui o "caderninho" e garante transparência na remuneração.
6. **Considerações Finais:** Resposta à problemática, limitações do projeto e trabalhos futuros.
