# ADR 002: Arquitetura do Módulo de PCP (Controle de Produtividade por Bipagem)

## Status
Aceito e Implementado

## Contexto
O sistema ERP Têxtil necessita de um módulo de PCP (Planejamento e Controle de Produção) robusto para gerenciar a produtividade no chão de fábrica. A solução precisa calcular a eficiência dos funcionários com base no tempo padrão das operações costuradas (bipagem de cupons) vs. o tempo ocioso (ocorrências).

O principal desafio é a **alta concorrência e a prevenção de fraudes** no chão de fábrica, garantindo que um ticket físico impresso não possa ser "bipado" (processado) duas vezes para inflar a produtividade do operador.

## Decisão

### 1. Modelagem de Dados
Adotamos uma abordagem que espelha o fluxo físico de uma confecção no banco de dados, utilizando as seguintes entidades no schema Multi-Tenant:
- **`Funcionario`**: Armazena informações de RH e cargas horárias (diária e mensal) para servirem de base de cálculo da eficiência.
- **`Pacote`**: Representa um lote físico da Ordem de Produção que trafega pela fábrica. Ele herda de `OrdemProducaoItem` a Grade (SKU: Tamanho, Cor) para que o lote seja estritamente homogêneo.
- **`Cupom`**: Representa o ticket que a costureira destaca do pacote ao finalizar a operação.
  - **Identificador Único (`codigoBarras`)**: Formado por `{OP}-{PACOTE}-{OPERACAO}` para garantir integridade.
  - **Tempo Centesimal**: Para cálculos precisos, todo o tempo é calculado em formato centesimal (ex: 1h e 30min = 1.5) previamente no momento da geração do cupom, evitando processamento desnecessário durante a bipagem.
- **`Apontamento`**: Prova imutável de trabalho vinculada a um `Cupom` e a um `Funcionario`. Há uma constraint `UNIQUE` na tabela de Apontamento ligando ao Cupom.
- **`Ocorrencia`**: Prova de tempo ocioso justificado (falta de luz, quebra de máquina) para ser deduzido do tempo base na fórmula final.

### 2. Prevenção de Fraude (Double-Scan)
No endpoint `POST /api/pcp/bipagem`:
1. Utilizamos a anotação `@Transactional` no serviço de negócio.
2. O sistema faz validação lógica (`status == PROCESSADO`) e relacional (Tentativa de inserção do Apontamento viola constraint `UNIQUE`).

### 3. Matemática da Produtividade
A fórmula estabelecida para a eficiência do funcionário no mês (`GET /api/pcp/produtividade`) é:

```
TempoProduzido = SUM(Cupom.tempoTotalCentesimal)
TempoOcorrencias = SUM(Ocorrencia.tempoDescontadoCentesimal)
BaseCalculo = Funcionario.cargaHorariaMensalPadrao - TempoOcorrencias
Eficiencia (%) = (TempoProduzido / BaseCalculo) * 100
```

### 4. Otimização de Interface (Frontend)
Foi desenvolvida uma tela React (`Bipagem.tsx`) otimizada para coletores de dados e leitores USB. 
- O input de texto opera em modo interceptador de `Enter` (padrão de scanners de código de barras).
- Requisições REST retornam um feedback visual instantâneo e grande (Verde/Vermelho) via `Snackbar`, com o input perdendo e recuperando foco em milissegundos.
- A tela elimina a necessidade de uso do mouse, mantendo o processo contínuo ("ultrarrápido").

## Consequências
- A geração prévia de cupons (através de `/gerar-pacotes`) exige espaço adicional em disco, mas torna a transação de bipagem significativamente mais leve e imutável.
- A restrição por SKU exige que a `OrdemProducao` tenha a grade estipulada via `OrdemProducaoItem` antes do início do processo produtivo.
