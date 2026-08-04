param (
    [string]$Token
)

$ErrorActionPreference = 'Stop'
Write-Host "=========================================="
Write-Host "Iniciando processo de analise do SonarQube"
Write-Host "=========================================="

if (-not $Token) {
    Write-Host "Nenhum token fornecido. Tentando executar sem token (pode falhar se a autenticacao for obrigatoria)."
}

Write-Host "`n[1/3] Executando testes e cobertura do Backend..."
Push-Location .\backend
Write-Host "Executando Maven via Docker..."
docker run --rm -v "${PWD}:/usr/src/app" -v "maven-repo:/root/.m2" -w /usr/src/app maven:3.9-amazoncorretto-17 mvn clean verify
Pop-Location

Write-Host "`n[2/3] Executando testes e cobertura do Frontend..."
Push-Location .\frontend
npm run test:coverage
Pop-Location

Write-Host "`n[3/3] Executando analise do Sonar Scanner..."
# Usa o container docker oficial do sonar-scanner conectado a rede do projeto
$NetworkName = "meuerpconfeccao_erp-net" # Adjust based on docker-compose project name, might be just "meu-erp-confeccao_erp-net"

# We can find network name automatically or just use host networking for simplicity on Windows/Mac
# 'http://host.docker.internal:9000' works well on Docker Desktop.
if ($Token) {
    docker run --rm -e SONAR_HOST_URL="http://host.docker.internal:9000" -e SONAR_TOKEN="$Token" -v "${PWD}:/usr/src" sonarsource/sonar-scanner-cli
} else {
    docker run --rm -e SONAR_HOST_URL="http://host.docker.internal:9000" -v "${PWD}:/usr/src" sonarsource/sonar-scanner-cli
}

Write-Host "`nAnalise concluida! Acesse http://localhost:9000 para ver os resultados."
