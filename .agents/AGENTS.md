# Regras do Projeto (Fashion ERP)

## Automate Git Commits and Pushes
Sempre que finalizar uma tarefa de código solicitada pelo usuário (como criar uma nova feature, arrumar um bug ou refatorar algo), você DEVE:
1. Verificar se o código está funcionando e sem erros (compilação/testes se aplicável).
2. Criar um ou mais commits semânticos (ex: `feat: ...`, `fix: ...`, `refactor: ...`) contendo todas as alterações feitas na tarefa.
3. Fazer o `git push` automaticamente para o repositório remoto.
4. Avisar ao usuário no final da resposta que as alterações já foram salvas e enviadas para o GitHub.
