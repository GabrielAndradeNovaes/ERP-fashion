@echo off
set SONAR_TOKEN=sqp_0580f72dd248b90893534db76efbbff16ee5ac5e

echo Iniciando analise do SonarQube para o backend...
docker run --rm --network meu-erp-confeccao_erp-net -v "%cd%\backend:/app" -w /app maven:3.9-eclipse-temurin-17 mvn clean compile sonar:sonar "-Dsonar.projectKey=backend" "-Dsonar.host.url=http://sonarqube:9000" "-Dsonar.token=%SONAR_TOKEN%"
echo Analise finalizada! Acesse http://localhost:9000 para ver os resultados.
pause
