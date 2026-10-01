# TCC: Roteiro para Apresentação em Slides

Este documento serve como guia estrutural para a confecção dos slides e ensaio da apresentação do TCC perante a banca avaliadora.

---

## Slide 1: Capa
- **Título do Trabalho:** Fashion ERP: Uma Plataforma SaaS Multi-Tenant para Gestão Têxtil.
- **Autor(es):** [Seu Nome]
- **Orientador:** [Nome do Orientador]
- **Instituição:** [Nome da Instituição]

## Slide 2: O Problema do Setor Têxtil
- **Contexto:** A indústria de confecção é fragmentada. Grande parte da força de trabalho opera de forma descentralizada.
- **Gargalo 1 - A Grade de SKUs:** Roupas possuem variações de "Cor" e "Tamanho". Softwares genéricos geram redundância extrema ao tentar controlar esse estoque.
- **Gargalo 2 - Controle do Chão de Fábrica:** Como remunerar corretamente costureiras baseando-se na produtividade real sem gerar um caos burocrático?

## Slide 3: A Proposta de Valor
- **O que é o Fashion ERP?** Um sistema ERP na nuvem (SaaS) focado estritamente nas regras e necessidades do setor de vestuário.
- **Acessibilidade:** Desenvolvido para pequenas e médias empresas, oferecendo estabilidade corporativa a baixo custo (compartilhamento de infraestrutura).

## Slide 4: Arquitetura do Sistema
- **Abordagem Multi-Tenant:** Um único servidor, mas com dados de clientes (Tenants) 100% isolados.
- **Padrão Adotado:** *Schema-per-Tenant*. O Banco Master cuida de logins e permissões; os Schemas virtuais (Tenant A, Tenant B) armazenam os dados transacionais de cada empresa.
- **Benefício:** Evita vazamento de informações e facilita a manutenção, não precisando instanciar um banco de dados novo para cada novo cliente.

## Slide 5: Tecnologias Utilizadas (Stack)
- **Backend:** Java 17 + Spring Boot 3 (API REST robusta, JPA e controle transacional).
- **Banco de Dados:** PostgreSQL 15.
- **Versionamento de DB:** Flyway (orquestra migrações nos múltiplos schemas automaticamente).
- **Frontend:** React + Vite + TypeScript. Componentização fluida e moderna utilizando Material UI (MUI).
- **Infraestrutura:** Containers Docker (Aplicação, Postgres, Redis).

## Slide 6: Modelagem Têxtil Especializada (O Domínio)
*Apresentar um diagrama simplificado de entidades, se possível:*
- **Produto Base + Variações:** Tabelas de Cores e Tamanhos cruzam com o Produto para materializar os SKUs de grade.
- **Ficha Técnica (BOM - Bill of Materials):** Define os insumos e tecidos exatos que a roupa consumirá, baseando-se no tamanho.
- **Roteiro de Operações:** Cálculo de tempo de costura. Define RPM da máquina e gera o "Standard Allowed Minute" (SAM).

## Slide 7: Gestão do Chão de Fábrica (Apontamentos)
- **O Fluxo PCP:** Ordem de Produção -> Geração de Pacotes -> Emissão de Cupons.
- **Tecnologia Aplicada:** Códigos de barra acompanham cada lote de roupas costuradas. A costureira, ao finalizar, utiliza a tela de Apontamentos para "bipar" os tickets.
- **Consequência:** O sistema processa o tempo ganho em tempo real, automatizando o controle de eficiência do RH produtivo.

## Slide 8: O Fluxo de Compras (Demonstração / Telas)
*(Momento ideal para colocar prints do sistema rodando)*
- Como as Ordens de Compra e Suprimentos foram desenhadas.
- O controle de aprovações e ações interativas (Emitir Pedido, Cancelar) construído em React, comunicando-se instantaneamente com o Backend protegido por JWT.

## Slide 9: Desafios Técnicos Enfrentados
- **Segurança e Interceptadores:** Criar uma lógica no backend (Filters/Aspects) capaz de ler o JWT em cada requisição e conectar o banco de dados dinamicamente no Schema daquele lojista sem que os Controllers saibam disso.
- **Isolamento Row-Level (RLS):** Lidar de forma contingencial com entidades matrizes utilizando interceptadores do Hibernate (Ex: Solução adotada para usuários SUPERADMIN no contexto multi-empresa).

## Slide 10: Conclusão
- O desenvolvimento comprovou que arquiteturas avançadas (SaaS) podem ser aplicadas para modernizar indústrias tradicionais.
- A união do ecossistema Java robusto com o frontend dinâmico em React resultou em uma aplicação escalável, de baixa latência e com excelente experiência de usuário.

## Slide 11: Dúvidas?
- Agradecimentos finais à banca.
- Contato.
