# Desafio QE Sicredi — Automação de Testes de API

Projeto de automação de testes REST desenvolvido com **REST Assured + Java 17 + JUnit 5**, cobrindo todos os endpoints da API [DummyJSON](https://dummyjson.com) conforme especificado no [desafio QE Sicredi](https://sicredi-desafio-qe.readme.io/reference/home).

---

## Pré-requisitos

### Com Docker (recomendado — sem instalar Java ou Maven)

| Ferramenta | Versão mínima | Instalação (macOS) |
|---|---|---|
| Docker Desktop | 24+ | [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop/) |

### Sem Docker (execução local)

| Ferramenta | Versão mínima | Instalação (macOS) |
|---|---|---|
| Java (JDK) | 17 | `brew install --cask temurin` |
| Maven | 3.8+ | `brew install maven` |
| Allure CLI (opcional) | 2.x | `brew install allure` |

---

## Subindo o projeto com Docker

### 1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd desafio_sicred
```

### 2. Execute com o comando principal

```bash
make        # equivalente a: make run
```

O que acontece automaticamente:

1. A imagem Docker é construída com Java 17 + Maven
2. As dependências Maven são baixadas e cacheadas na imagem
3. Os testes são executados contra `https://dummyjson.com`
4. O relatório HTML é gerado com `make report`
5. O nginx sobe e serve o relatório
6. O browser abre automaticamente em **http://localhost:5050**
7. O resultado dos testes é exibido no terminal

### 3. Encerre e limpe os recursos ao final

```bash
make clean
```

---

## Comandos Makefile

### Com Docker

| Comando | O que faz |
|---|---|
| `make` / `make run` | Constrói a imagem, executa os testes, aguarda o nginx subir, abre o relatório Allure em `http://localhost:5050` e exibe o resumo no terminal |
| `make test` | Sobe apenas o container de testes e exibe o resultado; não inicia o nginx |
| `make logs` | Acompanha os logs do container de testes em tempo real |
| `make clean` | Para todos os containers e remove os volumes criados |

### Localmente (requer Java 17 e Maven)

| Comando | O que faz |
|---|---|
| `make local-test` | Executa todos os testes |
| `make smoke` | Executa apenas os testes smoke (caminho feliz) |
| `make negative` | Executa apenas os testes negativos |
| `make local-test-class CLASS=AuthTest` | Executa uma classe de teste específica |
| `make local-test-class CLASS="AuthTest,ProductTest"` | Executa múltiplas classes |
| `make local-clean` | Limpa artefatos e executa todos os testes |
| `make report` | Gera o relatório Allure em HTML |
| `make serve` | Gera o relatório Allure e abre no browser |

---

## Pipeline CI/CD (GitHub Actions)

O arquivo `.github/workflows/ci.yml` define três jobs executados em sequência a cada push ou pull request nas branches `main` e `master`.

```
Build → Deploy (mock) → Test
```

| Job | O que faz |
|---|---|
| **Build** | Compila o projeto com `mvn compile test-compile`, valida que não há erros de compilação |
| **Deploy** | Simula as etapas de entrega: preparação do ambiente, deploy do artefato e health check |
| **Test** | Executa os testes com `mvn test`, gera o relatório Allure e publica os resultados |

### Onde ver os resultados

- **Resumo dos testes** — aba *Checks* do PR ou página do workflow, via `dorny/test-reporter`
- **Relatório Allure completo** — aba *Artifacts* do workflow (`allure-report`, retido por 7 dias)

### Executar manualmente

Na aba *Actions* do repositório, selecione `CI Pipeline` e clique em **Run workflow**.

---

## Executando os Testes Localmente (sem Docker)

### Todos os testes

```bash
make local-test
```

### Apenas testes smoke (caminho feliz)

```bash
make smoke
```

### Apenas testes negativos

```bash
make negative
```

### Uma classe de teste específica

```bash
make local-test-class CLASS=AuthTest
make local-test-class CLASS=ProductTest
make local-test-class CLASS=UserTest
make local-test-class CLASS=TestEndpointTest
```

### Um método de teste específico

```bash
make local-test-class CLASS="ProductTest#shouldReturn404ForNonExistentProductId"
```

### Múltiplas classes

```bash
make local-test-class CLASS="AuthTest,ProductTest"
```

### Limpar e executar

```bash
make local-clean
```

---

## Relatórios Locais (sem Docker)

### Allure — gera e abre no browser automaticamente

```bash
make serve
```

### Allure — apenas gera o HTML (sem abrir browser)

```bash
make local-test
make report
```

O relatório estará em `target/site/allure-maven-plugin/index.html`.

### Relatório padrão do Surefire

```bash
open target/surefire-reports/
```

---

## Estrutura do Projeto

```
desafio_sicred/
├── .github/workflows/ci.yml        # Pipeline GitHub Actions: Build → Deploy → Test
├── Makefile                        # Comandos do projeto: make run / make test / make logs / make clean
├── Dockerfile                      # Imagem Maven + Java 17; roda mvn test + allure:report
├── docker-compose.yml              # tests (Maven) + allure (nginx na porta 5050)
├── pom.xml                         # Dependências: REST Assured, JUnit 5, Allure, Lombok
└── src/test/java/com/sicredi/desafio/
    ├── config/
    │   └── ApiConfig.java          # Base URL, RequestSpec e ResponseSpec reutilizáveis
    ├── constants/
    │   └── Endpoints.java          # Paths de todos os endpoints como constantes
    ├── models/
    │   ├── request/
    │   │   ├── LoginRequest.java   # Body do POST /auth/login
    │   │   └── ProductRequest.java # Body do POST /products/add
    │   └── response/
    │       ├── ErrorResponse.java
    │       ├── LoginResponse.java
    │       ├── Product.java
    │       ├── ProductsResponse.java
    │       ├── User.java
    │       └── UsersResponse.java
    ├── services/                   # Camada de serviço: encapsula chamadas REST Assured
    │   ├── AuthService.java
    │   ├── ProductService.java
    │   ├── TestService.java
    │   └── UserService.java
    └── tests/
        ├── BaseTest.java           # @BeforeAll: busca usuário válido e obtém token JWT
        ├── AuthTest.java           # POST /auth/login | GET /auth/products
        ├── ProductTest.java        # GET /products | GET /products/{id} | POST /products/add
        ├── TestEndpointTest.java   # GET /test
        └── UserTest.java           # GET /users
```

---

## Endpoints Cobertos

| Método | Endpoint          | Auth | Cenários                                                        |
|--------|-------------------|------|-----------------------------------------------------------------|
| GET    | /test             | Não  | Status 200, body `{status: ok}`                                 |
| GET    | /users            | Não  | Lista não vazia, credenciais presentes, paginação               |
| POST   | /auth/login       | Não  | Token válido, dados do usuário, credenciais inválidas, body vazio |
| GET    | /auth/products    | Sim  | Token válido (200), sem token (401/403), token inválido (401)   |
| GET    | /products         | Não  | Lista, paginação, campos obrigatórios                           |
| GET    | /products/{id}    | Não  | Produto por id (200), id inexistente (404)                      |
| POST   | /products/add     | Não  | Criação com sucesso (201), id gerado automaticamente            |
