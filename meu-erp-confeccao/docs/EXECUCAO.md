# Guia de Execução e Testes

Este documento descreve como iniciar a aplicação e como realizar requisições para a API considerando a arquitetura multi-tenant do sistema.

## 1. Subindo o Projeto (Atualizado)

Para garantir que você está rodando a última versão do código (com todas as dependências e builds mais recentes), você deve executar o Docker Compose forçando o build das imagens:

```bash
docker-compose up -d --build
```

Isso irá subir:
- **PostgreSQL** (Porta 5433)
- **Redis** (Porta 6379)
- **Backend API** (Porta 8088)
- **Frontend** (Porta 3010)

## 2. Realizando Requisições (Multi-Tenant)

O sistema exige que todas as requisições informem o cabeçalho `X-TenantID` para que o roteamento de banco de dados funcione corretamente e saiba em qual cliente (tenant) operar.

Exemplo de tenant disponível: `tenant_1`

### 2.1. Testando a API via PowerShell (Invoke-RestMethod)

**Criar Produto (POST):**
```powershell
$body = @{
    codigo = "CAM-001"
    nome = "Camiseta Gola V"
    descricao = "Camiseta basica de algodao"
    skus = @(
        @{
            cor = "Azul"
            tamanho = "M"
            codigoBarras = "7891234567890"
            precoVenda = 49.90
        }
    )
} | ConvertTo-Json -Depth 5

$headers = @{
    "Content-Type" = "application/json"
    "X-TenantID" = "tenant_1"
}

Invoke-RestMethod -Uri "http://localhost:8088/api/catalog/produtos" -Method Post -Headers $headers -Body $body
```

**Listar Produtos (GET):**
```powershell
$headers = @{
    "X-TenantID" = "tenant_1"
}

Invoke-RestMethod -Uri "http://localhost:8088/api/catalog/produtos" -Method Get -Headers $headers
```

### 2.2. Testando a API via curl nativo do Windows

**Criar Produto (POST):**
```bash
curl.exe -X POST "http://localhost:8088/api/catalog/produtos" -H "Content-Type: application/json" -H "X-TenantID: tenant_1" -d "{\"codigo\":\"CAM-001\",\"nome\":\"Camiseta Gola V\",\"descricao\":\"Camiseta basica de algodao\",\"skus\":[{\"cor\":\"Azul\",\"tamanho\":\"M\",\"codigoBarras\":\"7891234567890\",\"precoVenda\":49.90}]}"
```

**Listar Produtos (GET):**
```bash
curl.exe -X GET "http://localhost:8088/api/catalog/produtos" -H "X-TenantID: tenant_1"
```

## 3. Testes Unitários e Cobertura (JaCoCo)

O projeto está configurado para gerar relatórios visuais de cobertura de código usando o JaCoCo. O relatório permite visualizar exatamente quais linhas de código foram testadas e quais não foram.

### 3.1. Como executar os testes

Como o projeto está rodando em Docker, você pode usar uma imagem do Maven temporária para rodar os testes sem precisar instalar o Java/Maven na sua máquina.

Na pasta raiz do projeto (`c:\projetos\fashion-erp\meu-erp-confeccao`), execute o seguinte comando no PowerShell ou CMD:

```bash
docker run --rm -v "${PWD}/backend:/usr/src/mymaven" -w /usr/src/mymaven maven:3.9-eclipse-temurin-17 mvn test jacoco:report
```

### 3.2. Como visualizar o relatório

Após a execução do comando acima, o Maven irá rodar todos os testes e gerar um relatório em HTML.
Para visualizá-lo:
1. Navegue pelo seu explorador de arquivos até a pasta: `backend\target\site\jacoco`
2. Dê um duplo clique no arquivo `index.html` para abri-lo no seu navegador (Chrome, Edge, etc).
3. Você poderá navegar pelos pacotes e ver a porcentagem de cobertura, clicando nas classes para ver as linhas verdes (testadas) e vermelhas (não testadas).
