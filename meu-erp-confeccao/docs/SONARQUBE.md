# Monitoramento Contínuo com SonarQube

O SonarQube foi implementado como a principal ferramenta para monitoramento contínuo da qualidade do código (Backend e Frontend), cobertura de testes, segurança e dívida técnica do ERP Confecção.

## 1. Inicializando o SonarQube

O SonarQube e seu banco de dados foram integrados ao ambiente Docker do projeto.
Para iniciar os serviços, utilize o comando:

```bash
docker-compose up -d sonarqube sonar-db
```

Após iniciar, aguarde alguns instantes e acesse:
**http://localhost:9000**

* **Login Padrão Inicial:** admin
* **Senha Padrão Inicial:** admin
*(Você será solicitado a alterar a senha no primeiro acesso).*

## 2. Gerando um Token de Acesso

Para realizar as análises através dos scripts fornecidos, você precisará de um Token do SonarQube:
1. Faça login no SonarQube (http://localhost:9000).
2. Vá em **My Account** (canto superior direito) > **Security**.
3. No campo "Generate Tokens", digite um nome (ex: "local-analysis"), escolha o tipo "User Token" (ou "Global Analysis Token" se preferir) e clique em **Generate**.
4. **Copie e guarde** o token gerado (você não poderá vê-lo novamente).

## 3. Executando uma Análise (Local)

Criamos scripts facilitadores que executam a compilação, rodagem dos testes de Backend (JaCoCo) e Frontend (LCOV) e, em seguida, realizam a análise com o `sonar-scanner-cli`.

### No Windows (PowerShell):
```powershell
.\run-sonar-analysis.ps1 -Token "SEU_TOKEN_GERADO"
```

### No Linux/Mac (Bash):
```bash
chmod +x run-sonar-analysis.sh
./run-sonar-analysis.sh "SEU_TOKEN_GERADO"
```

## 4. Configuração do Quality Gate

O SonarQube permite estabelecer um "Quality Gate" (Portão da Qualidade). Esta é a métrica oficial que define se o código está saudável o suficiente para ser implantado ou mesclado no branch principal.

Para configurar as regras solicitadas no projeto:
1. Vá no menu **Quality Gates** (no painel superior do SonarQube).
2. Clique em **Create** para criar um novo Quality Gate (ex: "ERP Quality Gate").
3. Adicione as seguintes condições (*Add Condition*):
   - **Coverage (Cobertura)**: is less than 80% (na aba "On Overall Code").
   - **Duplicated Lines (%)**: is greater than 3.0%.
   - **Bugs**: is greater than 0 (com severity Critical).
   - **Vulnerabilities**: is greater than 0 (com severity Critical).
   - **Security Hotspots Reviewed**: is less than 100%.
   - **Blocker Issues**: is greater than 0.
4. Vá na guia **Projects without Quality Gate** e marque o projeto `meu-erp-confeccao` para associá-lo a este novo gate.

> **Nota sobre o Quality Gate:** O Quality Gate ideal deve ser configurado via interface Web, pois algumas métricas de restrição dependem de plugins e não são oficialmente suportadas via configuração em arquivos `.properties` no Community Edition.

## 5. Como os módulos foram configurados?

As configurações centralizadas encontram-se em `sonar-project.properties` na raiz do projeto.

* **Backend**: Configurado para ser lido em `src/main/java` e `src/test/java`. Utiliza o plugin Maven (`jacoco-maven-plugin`) para compilar as classes (`target/classes`) e gerar o relatório `target/site/jacoco/jacoco.xml`.
* **Frontend**: Configurado para ler arquivos de código no `src/`. O Vite foi incrementado com `Vitest` + `v8 coverage` para gerar o relatório em `coverage/lcov.info`.
* **Exclusões**: Estão ignorados arquivos não-essenciais como `node_modules`, `dist`, classes de teste (não devem ser contadas como código sujo ou descoberto) e dependências temporárias.

## 6. Adicionando Novos Módulos ao Monitoramento

Se o projeto ERP crescer e novos módulos (ex: um app mobile) precisarem ser incluídos:
1. Adicione o nome do módulo na propriedade `sonar.modules` no `sonar-project.properties`.
2. Adicione as propriedades correspondentes para aquele módulo (semelhante ao `backend.sonar...` ou `frontend.sonar...`), definindo seus próprios caminhos de sources e relatórios de cobertura.
3. Certifique-se de que o script de análise (ps1 e sh) rode os comandos necessários de build e teste para esse novo módulo antes de disparar o scanner geral.
