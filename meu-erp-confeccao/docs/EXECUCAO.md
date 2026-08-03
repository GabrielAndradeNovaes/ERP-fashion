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
