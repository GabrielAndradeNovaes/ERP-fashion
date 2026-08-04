# ADR 002: Motor de Ordens de Produção e Consumo de Estoque

## Status
Aceito

## Contexto
O sistema necessita gerenciar a produção (PCP), emitindo Ordens de Produção (OPs) e abatendo as matérias-primas do estoque com base nas Fichas Técnicas (BOM - Bill of Materials). Precisávamos definir o momento exato do consumo do estoque e a persistência do saldo atual, dado que a aplicação opera em arquitetura Multi-Tenant.

## Decisão
1. **Status da Ordem:** A OP foi estruturada com os estados `CADASTRADA`, `EM_ANDAMENTO`, `CONCLUIDA` e `CANCELADA`.
2. **Momento da Baixa de Estoque:** O estoque só será deduzido quando a OP mudar para o status `EM_ANDAMENTO` (Iniciada). Não faremos reserva de estoque no status `CADASTRADA` para simplificar a operação inicial do almoxarifado.
3. **Persistência de Saldo:** Adicionamos a coluna `quantidade_atual` na tabela `materiais`. Ao invés de somar o histórico inteiro de movimentações (`estoque_movimentacoes`) a cada requisição GET, o serviço de `EstoqueMovimentacaoService` atualizará o saldo em tempo real na tabela de Materiais, otimizando a performance do Frontend.
4. **Isolamento Tenant:** As novas tabelas (`ordens_producao`) e colunas foram aplicadas a todos os schemas configurados (`master` e `tenant_1`). A chave estrangeira (`fk_op_ficha`) vincula corretamente a OP à receita do modelo.

## Consequências
- **Positivas:** 
  - Visualização de saldos de estoque é instantânea para o Frontend.
  - Processo de Início de Produção é atômico (Gera movimentos de saída e subtrai saldo dentro da mesma transação de banco de dados, protegida pelo `@Transactional`).
- **Negativas/Atenção:** 
  - Pode ocorrer saldo negativo de estoque se a fábrica produzir sem dar entrada prévia nos materiais. Por decisão de negócio inicial, decidimos não travar o processo, mantendo o aviso apenas, mas futuramente pode ser configurável. (No momento, o banco e a API permitem o valor deduzir indefinidamente se o Backend não proibir).
