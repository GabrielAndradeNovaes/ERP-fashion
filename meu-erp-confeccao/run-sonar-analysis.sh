#!/bin/bash
set -e

TOKEN=$1

echo "=========================================="
echo "Iniciando processo de analise do SonarQube"
echo "=========================================="

if [ -z "$TOKEN" ]; then
    echo "Nenhum token fornecido. Tentando executar sem token."
fi

echo -e "\n[1/3] Executando testes e cobertura do Backend..."
cd backend
echo "Executando Maven via Docker..."
docker run --rm -v "$(pwd):/usr/src/app" -v "maven-repo:/root/.m2" -w /usr/src/app maven:3.9-amazoncorretto-17 mvn clean verify
cd ..

echo -e "\n[2/3] Executando testes e cobertura do Frontend..."
cd frontend
npm run test:coverage
cd ..

echo -e "\n[3/3] Executando analise do Sonar Scanner..."
# host.docker.internal works for Linux (if configured) and Mac/Windows Desktop.
# Alternatively, use the bridge network IP if on pure Linux without host.docker.internal
if [ -n "$TOKEN" ]; then
    docker run --rm --add-host=host.docker.internal:host-gateway -e SONAR_HOST_URL="http://host.docker.internal:9000" -e SONAR_TOKEN="$TOKEN" -v "$(pwd):/usr/src" sonarsource/sonar-scanner-cli
else
    docker run --rm --add-host=host.docker.internal:host-gateway -e SONAR_HOST_URL="http://host.docker.internal:9000" -v "$(pwd):/usr/src" sonarsource/sonar-scanner-cli
fi

echo -e "\nAnalise concluida! Acesse http://localhost:9000 para ver os resultados."
