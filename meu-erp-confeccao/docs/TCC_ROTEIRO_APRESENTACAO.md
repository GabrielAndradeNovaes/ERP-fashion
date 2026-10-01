# TCC: Roteiro para Apresentação em Slides

Este documento serve como guia estrutural para a confecção dos slides e ensaio da apresentação focada em **Transparência nas Relações de Trabalho e Inclusão Digital**.

---

## Slide 1: Capa
- **Título do Trabalho:** Transparência e Rastreabilidade no Setor Têxtil: Uma Abordagem SaaS Multi-Tenant para a Inclusão Digital e Formalização do Chão de Fábrica.
- **Autor(es):** [Seu Nome]
- **Orientador:** [Nome do Orientador]
- **Instituição:** [Nome da Instituição]

## Slide 2: Contexto e Problemática (A Dor Social)
- **O Cenário:** A indústria têxtil, especialmente no nicho de oficinas terceirizadas e micro-facções, opera sob altos índices de informalidade.
- **A Ferramenta Atual:** O "caderninho" de anotações manuais. 
- **O Problema:** Como a falta de rastreabilidade na produção gera subnotificação de trabalho, conflitos de remuneração (horas não pagas corretamente) e mantém os microempreendedores reféns da falta de dados?

## Slide 3: A Solução e Objetivos
- **O Produto (Fashion ERP):** Um sistema SaaS acessível focado estritamente nas regras e necessidades do chão de fábrica do vestuário.
- **Objetivo Principal:** Mitigar a informalidade através de um modelo arquitetural de nuvem (SaaS Multi-Tenant) e rastreabilidade via digitalização da produção (códigos de barras).
- **O Viés Inclusivo:** Como quebrar as barreiras de custo em TI permite que PMEs acessem tecnologias de classe corporativa.

## Slide 4: Arquitetura de Software Multi-Tenant
- **O que é Multi-Tenant?** Explicar como um único servidor hospeda múltiplos clientes com segurança.
- **Abordagem Schema-per-Tenant:** Demonstração visual de como o Banco Master (Control Plane) isola logicamente cada cliente (Tenant A, Tenant B) dentro do PostgreSQL.
- **Justificativa Tecnológica:** Redução drástica do custo de infraestrutura comparada a servidores isolados.

## Slide 5: Tecnologias e Implementação
- **Backend Robusto:** Java 17 + Spring Boot 3 + PostgreSQL (garantia de transações consistentes e seguras).
- **Frontend Ágil e Moderno:** React + Vite + TypeScript.
- **Isolamento de Requisições:** Uso de JWT (Tokens) interligados a Filtros do Hibernate para impedir o vazamento de dados entre empresas.
- **Gestão de Banco de Dados:** Flyway orquestrando *migrations* dinamicamente.

## Slide 6: Modelagem do Domínio Têxtil (O Motor do Sistema)
- **Ficha Técnica e Operações:** Cadastro do roteiro exato de fabricação (RPM da máquina, dificuldade do tecido, etc.) para o cálculo do tempo padrão justo (SAM - Standard Allowed Minute).
- **O Fluxo PCP:** Ordem de Produção -> Geração do Lote (Pacote) -> Geração de Cupons Rastreados.

## Slide 7: Inclusão Digital e Transparência na Prática
- **A Bipagem (Substituindo o Caderninho):** Demonstração do funcionamento da tela de Apontamentos.
- **A Proteção Bilateral:** 
  - **Para a Costureira:** Registro digital imutável de peças produzidas.
  - **Para o Dono da Facção:** Certeza e velocidade sobre o fluxo de produção, eliminando retrabalhos em cálculos de produtividade e pagamentos.

## Slide 8: O Impacto Social Direto
- Pagamento justo e incontestável baseado em dados (Tempo Teórico vs Tempo Real).
- Empoderamento do microempresário, possibilitando decisões baseadas em gargalos produtivos e métricas claras.
- A transição gradual da informalidade estrutural para a formalização das relações trabalhistas no setor têxtil.

## Slide 9: Considerações Finais
- **O Papel da Engenharia de Software:** Como soluções arquiteturais avançadas podem resolver problemas sociais arraigados.
- **Próximos Passos (Trabalhos Futuros):** Integração com relógios de ponto, aplicativos móveis para as costureiras acompanharem sua renda em tempo real e consolidação de notas fiscais (NFe).

## Slide 10: Dúvidas?
- Agradecimentos finais à banca.
- Contato.
