# Sistema de Agendamentos — API REST

Backend de gerenciamento de agendamentos para prestador de serviços (MVP focado em técnico de ar-condicionado). Construído com **Java 21 + Spring Boot**, autenticação via **JWT**, banco **PostgreSQL** e documentação interativa via **Swagger UI**.

---

## Sumário

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Camadas da Aplicação](#camadas-da-aplicação)
- [Entidades e Banco de Dados](#entidades-e-banco-de-dados)
- [Autenticação e Autorização](#autenticação-e-autorização)
- [Endpoints da API](#endpoints-da-api)
- [Status de Agendamento](#status-de-agendamento)
- [Configuração de Ambiente](#configuração-de-ambiente)
- [Rodando Localmente](#rodando-localmente)
- [Docker](#docker)
- [Deploy (Render)](#deploy-render)
- [Documentação Swagger](#documentação-swagger)
- [Contexto e Refatoração](#contexto-e-refatoração)

---

## Visão Geral

O sistema permite que um **prestador (ADMIN/técnico)** gerencie sua agenda de serviços e que **clientes** realizem agendamentos online. O fluxo principal é:

1. Cliente se cadastra e faz login.
2. Cliente consulta serviços disponíveis e horários livres.
3. Cliente cria um agendamento (fica com status `PENDING`).
4. Admin aceita ou rejeita o agendamento.
5. Sistema envia e-mail de confirmação quando o status muda para `CONFIRMED`.
6. Cliente pode cancelar seu próprio agendamento.

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 4.0.3 | Framework base |
| Spring Security | — | Segurança e autorização |
| Spring Data JPA / Hibernate | — | Persistência |
| PostgreSQL | 15 | Banco de dados |
| JWT (Auth0 `java-jwt`) | — | Autenticação stateless |
| MapStruct | — | Mapeamento DTO ↔ entidade |
| Lombok | — | Redução de boilerplate |
| SpringDoc OpenAPI | — | Swagger UI |
| Spring Mail | — | Envio de e-mails |
| Maven | — | Build e dependências |
| Docker / Docker Compose | — | Containerização |
| Render | — | Plataforma de deploy |

---

## Estrutura do Projeto

```
src/
└── main/
│   ├── java/com/swetonyancelmo/agendamentos/
│   │   ├── AgendamentosApplication.java       # Ponto de entrada da aplicação
│   │   ├── config/                            # Configurações de segurança, JWT e Swagger
│   │   │   ├── AuthConfig.java
│   │   │   ├── JWTUserData.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── SecurityFilter.java
│   │   │   ├── SwaggerConfig.java
│   │   │   └── TokenConfig.java
│   │   ├── controller/                        # Controllers REST
│   │   │   └── docs/                          # Interfaces com anotações OpenAPI
│   │   ├── dtos/
│   │   │   ├── request/                       # DTOs de entrada (requisição)
│   │   │   └── response/                      # DTOs de saída (resposta)
│   │   ├── exceptions/                        # Exceções e handler global
│   │   ├── mapper/                            # Interfaces MapStruct
│   │   ├── models/                            # Entidades JPA
│   │   │   └── enums/                         # Enums (ex: AppointmentStatus, Role)
│   │   ├── repositories/                      # Interfaces Spring Data JPA
│   │   └── services/                          # Regras de negócio
│   └── resources/
│       ├── application.yml                    # Configuração principal (usa variáveis de ambiente)
│       └── application-example.yml            # Exemplo de configuração local
└── test/
    └── java/com/swetonyancelmo/agendamentos/
        └── AgendamentosApplicationTests.java  # Teste de contexto Spring
```

---

## Camadas da Aplicação

### `config/`
Toda a configuração de infraestrutura da aplicação.

- **`SecurityConfig`** — define as regras de acesso HTTP (rotas públicas vs. autenticadas, CORS para `localhost:3000` e `localhost:5173`).
- **`SecurityFilter`** — filtro que intercepta cada requisição, valida o JWT do header `Authorization` e popula o `SecurityContext`.
- **`TokenConfig`** — gera e valida tokens JWT com claims de `userId`, `role` e subject (e-mail). Expiração de ~24h.
- **`AuthConfig`** — configura o `AuthenticationManager` e o `UserDetailsService` (busca `Business` por e-mail).
- **`SwaggerConfig`** — configura o esquema Bearer para o Swagger UI.

### `controller/`
Recebe as requisições HTTP, delega para os `services` e retorna as respostas. Cada controller possui uma interface correspondente em `controller/docs/` com anotações `@Operation` e `@Tag` do OpenAPI, mantendo o controller limpo.

### `dtos/`
Objetos de transferência de dados — nunca expõe as entidades diretamente na API.

- **`request/`** — dados que chegam do cliente (ex: `CreateAppointmentRequest`, `BusinessLoginDto`).
- **`response/`** — dados retornados pela API (ex: `AppointmentResponse`, `TokenResponse`).

### `mapper/`
Interfaces MapStruct que convertem entidades em DTOs de resposta e vice-versa, sem código manual.

### `models/`
Entidades JPA mapeadas para tabelas do PostgreSQL. O Hibernate cria/atualiza o schema automaticamente (`ddl-auto: update`).

### `repositories/`
Interfaces que estendem `JpaRepository`, fornecendo operações CRUD e queries derivadas de nomes de métodos sem necessidade de SQL manual.

### `services/`
Contém toda a lógica de negócio. É aqui que ficam as validações, regras de agendamento, envio de e-mail, etc. Os controllers apenas orquestram chamadas para os services.

### `exceptions/`
`GlobalExceptionHandler` (anotado com `@RestControllerAdvice`) captura exceções lançadas pelos services e retorna respostas HTTP padronizadas, evitando que stack traces vazem para o cliente.

---

## Entidades e Banco de Dados

O schema é gerenciado automaticamente pelo Hibernate com base nas entidades. As principais tabelas são:

| Tabela | Descrição |
|---|---|
| `tb_businesses` | Prestadores de serviço / contas admin |
| `tb_customers` | Clientes cadastrados |
| `tb_services` | Serviços oferecidos (ex: limpeza, instalação) |
| `tb_availabilities` | Horários disponíveis para agendamento |
| `tb_appointments` | Agendamentos realizados pelos clientes |

**Relacionamentos principais:**
- `Appointment` → `Customer`, `Service`, `Business`
- `Service` → `Business`
- `Availability` → `Business`

---

## Autenticação e Autorização

A API usa **JWT (Bearer Token)** stateless. Não há sessão no servidor.

### Fluxo
1. Cliente/admin faz login via endpoint de auth.
2. Recebe um JWT no response.
3. Envia o token em todas as requisições autenticadas no header: `Authorization: Bearer <token>`.

### Roles
| Role | Descrição |
|---|---|
| `ROLE_BUSINESS` | Admin / prestador de serviço (técnico) |
| `ROLE_CUSTOMER` | Cliente |

### Onde está o código
- Geração e validação do token: `TokenConfig.java`
- Filtro de autenticação por requisição: `SecurityFilter.java`
- Regras de acesso por rota: `SecurityConfig.java`
- Lógica de login/registro: `AuthService` (em `services/`)

---

## Endpoints da API

> Documentação interativa completa disponível em `/swagger-ui/index.html` com a aplicação rodando.

### Autenticação — `/auth` (público)

| Método | Rota | Descrição | Role |
|---|---|---|---|
| `POST` | `/auth/register` | Cadastro de business/admin | Público |
| `POST` | `/auth/login` | Login de business/admin → retorna JWT | Público |
| `POST` | `/auth/customer/register` | Cadastro de cliente | Público |
| `POST` | `/auth/customer/login` | Login de cliente → retorna JWT | Público |

### Empresas / Admin — `/api/business`

| Método | Rota | Descrição | Role |
|---|---|---|---|
| `GET` | `/api/business` | Lista todos os businesses | BUSINESS ou CUSTOMER |

### Serviços — `/api/business/services`

| Método | Rota | Descrição | Role |
|---|---|---|---|
| `POST` | `/api/business/services` | Criar serviço | BUSINESS |
| `GET` | `/api/business/services` | Listar serviços do admin autenticado | BUSINESS |
| `GET` | `/api/business/services/{businessId}` | Listar serviços de um business | BUSINESS ou CUSTOMER |

### Disponibilidade — `/api/business/availability`

| Método | Rota | Descrição | Role |
|---|---|---|---|
| `POST` | `/api/business/availability` | Criar horário disponível | BUSINESS |
| `GET` | `/api/business/availability/{businessId}` | Listar disponibilidades | BUSINESS ou CUSTOMER |
| `DELETE` | `/api/business/availability/{availabilityId}` | Remover disponibilidade | BUSINESS |

### Clientes — `/api/customers`

| Método | Rota | Descrição | Role |
|---|---|---|---|
| `GET` | `/api/customers` | Listar clientes | Autenticado |

### Agendamentos — `/api/appointments`

| Método | Rota | Descrição | Role |
|---|---|---|---|
| `POST` | `/api/appointments` | Criar agendamento | CUSTOMER |
| `GET` | `/api/appointments/me` | Agendamentos do cliente autenticado | CUSTOMER |
| `GET` | `/api/appointments/customer` | Alias do endpoint acima | CUSTOMER |
| `GET` | `/api/appointments/business/{businessId}` | Agendamentos de um business (filtro por `?status=`) | BUSINESS |
| `PATCH` | `/api/appointments/{appointmentId}/status` | Aceitar ou rejeitar agendamento | BUSINESS |
| `PATCH` | `/api/appointments/{appointmentId}/cancel` | Cancelar agendamento | CUSTOMER |

---

## Status de Agendamento

```java
PENDING    // criado pelo cliente, aguardando aprovação do técnico
CONFIRMED  // aceito pelo técnico (dispara e-mail de confirmação)
CANCELLED  // cancelado pelo cliente ou rejeitado pelo técnico
COMPLETED  // serviço finalizado
```

**Regras:**
- Apenas o **ADMIN** pode aceitar ou rejeitar.
- Apenas o **CUSTOMER dono** pode cancelar seu próprio agendamento.
- Não é possível alterar o status de um agendamento já `CANCELLED` ou `COMPLETED`.

---

## Configuração de Ambiente

A aplicação usa variáveis de ambiente definidas no `application.yml`. Copie o arquivo `application-example.yml` como base para criar sua configuração local.

### Variáveis necessárias

| Variável | Descrição | Exemplo |
|---|---|---|
| `JWT_SECRET` | Segredo para assinar os tokens JWT | `minha-chave-secreta-256bits` |
| `DB_HOST` | Host do PostgreSQL | `localhost` |
| `DB_PORT` | Porta do PostgreSQL | `5433` |
| `DB_NAME` | Nome do banco de dados | `agendamento_db` |
| `DB_USERNAME` | Usuário do banco | `admin` |
| `DB_PASSWORD` | Senha do banco | `123456` |
| `PORT` | Porta da aplicação (padrão: `8080`) | `8080` |
| `MAIL_HOST` | Host SMTP | `smtp.gmail.com` |
| `MAIL_PORT` | Porta SMTP | `587` |
| `MAIL_USERNAME` | E-mail remetente | `seuemail@gmail.com` |
| `MAIL_PASSWORD` | Senha de app do e-mail | `sua-senha-app` |

### Configuração local recomendada

Crie o arquivo `src/main/resources/application-local.yml` (já está no `.gitignore`) com os valores reais para desenvolvimento. O Spring Boot carrega profiles automaticamente — para ativar, passe `-Dspring.profiles.active=local` na inicialização ou configure na sua IDE.

---

## Rodando Localmente

### Pré-requisitos

- Java 21+
- Docker e Docker Compose (para o banco)
- Maven (ou use o wrapper `mvnw` incluído no projeto)

### 1. Subir o banco de dados

```bash
docker-compose up -d
```

Isso sobe um PostgreSQL 15 na porta **5433** com:
- Usuário: `admin`
- Senha: `123456`
- Banco: `agendamento_db`

### 2. Configurar variáveis de ambiente

Exporte as variáveis no terminal ou crie um `application-local.yml`:

```bash
# Windows PowerShell
$env:JWT_SECRET="sua-chave-secreta"
$env:DB_HOST="localhost"
$env:DB_PORT="5433"
$env:DB_NAME="agendamento_db"
$env:DB_USERNAME="admin"
$env:DB_PASSWORD="123456"
```

### 3. Iniciar a aplicação

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

### 4. Rodar os testes

```bash
.\mvnw.cmd test        # Windows
./mvnw test            # Linux / macOS
```

---

## Docker

O projeto inclui um `Dockerfile` multi-stage que compila o JAR com Maven e depois roda em uma imagem JRE 21 enxuta.

```bash
# Build da imagem
docker build -t agendamentos-api .

# Rodar o container
docker run -p 8080:8080 \
  -e JWT_SECRET=sua-chave \
  -e DB_HOST=host-do-banco \
  -e DB_PORT=5432 \
  -e DB_NAME=agendamento_db \
  -e DB_USERNAME=admin \
  -e DB_PASSWORD=123456 \
  agendamentos-api
```

---

## Deploy (Render)

O arquivo `render.yaml` configura o deploy automático na plataforma [Render](https://render.com):

- Tipo: serviço Docker
- Banco: PostgreSQL gerenciado pelo Render
- Variáveis de ambiente configuradas no painel do Render (JWT gerado automaticamente, credenciais de banco injetadas, SMTP configurável)

Para fazer o deploy, basta conectar o repositório ao Render e ele usará o `render.yaml` como blueprint.

---

## Documentação Swagger

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui/index.html
```

A documentação é gerada automaticamente pelo SpringDoc OpenAPI com base nas anotações nos controllers (interfaces em `controller/docs/`). Para testar endpoints autenticados, clique em **Authorize** e informe o token JWT no formato `Bearer <token>`.

---

## Contexto e Refatoração

O sistema foi originalmente projetado como **multi-tenant** (múltiplas empresas). O escopo foi simplificado para atender a um **único prestador de serviços** (técnico de ar-condicionado), tornando a solução mais focada e fácil de manter.

O arquivo `REFAC.md` na raiz do projeto documenta detalhadamente:
- O que foi removido (modelo multi-empresa)
- A nova arquitetura proposta
- Novas funcionalidades planejadas (endereço no agendamento, painel admin, etc.)
- Próximos passos do MVP

Se você for implementar algo novo, **leia o `REFAC.md` antes** para entender a direção planejada do projeto e evitar reintroduzir complexidade desnecessária.

---

## Próximos Passos do MVP

- [ ] Remover entidade `Business` e simplificar para único prestador
- [ ] Unificar autenticação com roles `ADMIN` / `CUSTOMER`
- [ ] Adicionar endereço (`address`, `city`, `reference`) no agendamento
- [ ] Geração automática de horários disponíveis (ex: 08h às 18h)
- [ ] Painel administrativo (frontend)
- [ ] Notificações via WhatsApp
- [ ] Integração com pagamentos
