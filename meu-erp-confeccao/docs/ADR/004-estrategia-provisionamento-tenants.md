# ADR 004: Estratégia de Provisionamento Automatizado de Tenants (Onboarding)

## Contexto
No modelo SaaS Multi-Tenant da aplicação, registrar um novo cliente ("Tenant") exige a orquestração de diversas operações de infraestrutura e dados:
1. Registro na tabela `clientes_tenant` (Schema Master).
2. Criação do Schema lógico do cliente no PostgreSQL (`CREATE SCHEMA`).
3. Execução das migrações do banco de dados específicas para tenants usando **Flyway**.
4. Inserção de dados vitais de inicialização ("Seed"), como o primeiro Usuário Administrador.

Anteriormente, essa orquestração era síncrona. Isso apresentava dois grandes problemas:
- **Resiliência:** Se o Flyway falhasse no meio do processo, o schema ficaria sujo ou incompleto, mas o status no Master poderia estar inconsistente.
- **Experiência do Usuário (UX):** O painel do `SUPERADMIN` no frontend ficava travado aguardando uma requisição longa, sujeita a timeouts da rede.

## Decisão
Foi decidido adotar uma arquitetura de **Orquestração Assíncrona com Máquina de Estados e Compensação (Rollback)**:

### 1. Assincronismo e Polling (Fire and Forget + Monitoramento)
A API do Backoffice (`AdminTenantController`) responde imediatamente com `202 Accepted` assim que o registro básico é inserido no banco de dados Master com o status `PENDENTE`. 
O processamento pesado (Infraestrutura, Migração e Seed) é despachado para uma thread de background utilizando o mecanismo `@Async` nativo do Spring Boot.
O Frontend (Painel Admin) realiza um *Long Polling* inteligente, consultando a API a cada 3 segundos apenas se houver algum tenant na lista com o status `PENDENTE` ou `CRIANDO_INFRA`.

### 2. Máquina de Estados (Lifecycle)
O ciclo de vida do provisionamento passa pelas seguintes transições de estado (`status`):
- `PENDENTE`: Registro criado no master. Aguardando a thread assíncrona iniciar.
- `CRIANDO_INFRA`: Thread assíncrona capturou o processo e está criando o Schema ou rodando o Flyway.
- `ATIVO`: Processo concluído com sucesso. O cliente já pode logar.
- `FALHA`: Ocorreu uma exceção no meio do processo.

### 3. Resiliência e Compensação (Saga Pattern Simplificado)
Como a criação de Schema via DDL (`CREATE SCHEMA`) não pode ser revertida de forma confiável pelo gerenciador transacional padrão do Spring (`@Transactional`), implementamos uma estratégia de **Compensação Manual**:
- Todo o bloco de orquestração está envolto em um `try-catch`.
- Se uma falha ocorrer durante o Flyway ou no Seed, a exceção é capturada, e um bloco de compensação é executado: `DROP SCHEMA IF EXISTS <schema_name> CASCADE`.
- O status do cliente na tabela Master é atualizado para `FALHA`, sinalizando ao Backoffice que a operação falhou e o ambiente não está ativo, mantendo o banco de dados limpo de schemas fragmentados.

## Consequências (Trade-offs)
- **Positivo:** A interface do administrador não sofre bloqueios. Redução drástica de falhas por timeout.
- **Positivo:** Garante que o banco de dados principal não acumulará schemas defeituosos ("lixo de infraestrutura").
- **Negativo:** O uso de threads de background (`@Async`) sem uma fila externa (como RabbitMQ ou Kafka) significa que se a aplicação for morta/reiniciada no exato momento em que um tenant estiver provisionando, aquele tenant ficará eternamente no status `CRIANDO_INFRA` (pois não há um broker de mensagens garantindo reentrega). Para esse MVP, é aceitável, mas em escalas maiores exigirá um Message Broker.
