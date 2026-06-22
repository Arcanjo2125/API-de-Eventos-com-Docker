# Sistema de Controle de Eventos

## Nome da Equipe
[CodeEvent]

## Tema Escolhido
Tema 12 — Sistema de Controle de Eventos

## Integrantes
- [Nome:Lucas Gonçalves Rego]
- [Matricula: 1250203728]
- [Nome:Ingrid Gonçalves Torres ]
- [Matricula: 1250203724]

## Tecnologias Usadas
- Java 17
- Spring Boot 3.2
- Spring Data JPA
- Thymeleaf
- PostgreSQL (em container Docker)
- Bootstrap removido — UI própria, construída em CSS puro (tema "ingresso")
- Docker / Docker Compose
- Maven

## Funcionalidades Implementadas

### Obrigatórias ✅
- Listagem de eventos
- Cadastro de novos eventos
- Funcionalidade **Alterar** (editar evento existente)
- Funcionalidade **Excluir** (remover evento com confirmação)
- Banco de dados PostgreSQL rodando em container
- Integração completa Frontend + Backend + Banco de Dados

### Extras ✅
- Mensagens de **sucesso** e **erro** para o usuário em todas as operações
- Validação de campos obrigatórios (`@NotBlank`, `@NotNull`, `@Min`) com mensagens de erro exibidas diretamente no formulário
- Interface redesenhada do zero: cada evento é exibido como um "ingresso", com canhoto perfurado separando informações e ações — layout responsivo (desktop e mobile)
- Aplicação 100% containerizada com Docker (backend + banco de dados) comunicando-se através de uma rede Docker dedicada

## Campos do Sistema
| Campo | Tipo | Obrigatório |
|-------|------|-------------|
| Nome do Evento | Texto | Sim |
| Local | Texto | Sim |
| Data | Data | Sim |
| Capacidade | Número | Sim (mínimo 1) |

---

## Como Executar com Docker (recomendado)

### Pré-requisitos
- Docker
- Docker Compose (já incluso no Docker Desktop)

### Passo a passo

1. Entre na pasta do projeto:
```bash
cd eventos
```

2. Suba os containers (build da imagem do backend + PostgreSQL):
```bash
docker compose up --build
```

Esse comando:
- Cria a rede Docker `eventos-network`;
- Sobe o container `eventos-db` com a imagem oficial do **postgres:16-alpine**, expondo a porta `5432`, com usuário, senha e banco configurados via variáveis de ambiente;
- Constrói a imagem do backend a partir do `Dockerfile` (build multi-estágio com Maven + JRE) e sobe o container `eventos-backend`, expondo a porta `8080`;
- Conecta os dois containers à mesma rede `eventos-network`, permitindo que o backend acesse o banco pelo hostname `db` (nome do serviço no `docker-compose.yml`), sem precisar do IP do container.

3. Acesse no navegador:
```
http://localhost:8080/eventos
```

4. Para encerrar os containers:
```bash
docker compose down
```
(Os dados do PostgreSQL persistem no volume `eventos-db-data` mesmo após o `down`; use `docker compose down -v` para também remover os dados.)

### Executando os containers manualmente (sem docker-compose)

Caso prefira demonstrar a criação da rede e dos containers passo a passo:

```bash
# 1. Criar a rede
docker network create eventos-network

# 2. Subir o banco de dados
docker run -d --name eventos-db \
  --network eventos-network \
  -e POSTGRES_DB=eventosdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine

# 3. Construir a imagem do backend
docker build -t eventos-backend .

# 4. Subir o backend, apontando para o banco pelo nome do container
docker run -d --name eventos-backend \
  --network eventos-network \
  -e DB_HOST=eventos-db \
  -e DB_PORT=5432 \
  -e DB_NAME=eventosdb \
  -e DB_USER=postgres \
  -e DB_PASSWORD=postgres \
  -p 8080:8080 \
  eventos-backend
```

---

## Como Executar Localmente (sem Docker)

### Pré-requisitos
- Java 17 ou superior instalado
- Maven instalado (ou usar o wrapper incluído)
- Um PostgreSQL acessível (local ou em container), com banco `eventosdb` criado

### Passo a passo

1. Suba um PostgreSQL local ou via Docker:
```bash
docker run -d --name eventos-db -p 5432:5432 \
  -e POSTGRES_DB=eventosdb -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres \
  postgres:16-alpine
```

2. Abra o terminal na pasta do projeto:
```bash
cd eventos
```

3. Execute o projeto com Maven (as variáveis de ambiente têm valores padrão apontando para `localhost:5432`):
```bash
mvn spring-boot:run
```

4. Acesse no navegador:
```
http://localhost:8080/eventos
```

### Usando IntelliJ IDEA / VS Code
1. Importe o projeto como **Maven Project**
2. Aguarde o download das dependências
3. Garanta que o PostgreSQL esteja rodando (passo 1 acima)
4. Execute a classe `EventosApplication.java`
5. Acesse `http://localhost:8080/eventos`

---

## Variáveis de Ambiente do Backend
| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `DB_HOST` | Host do PostgreSQL | `localhost` |
| `DB_PORT` | Porta do PostgreSQL | `5432` |
| `DB_NAME` | Nome do banco de dados | `eventosdb` |
| `DB_USER` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | `postgres` |

## Estrutura do Projeto
```
eventos/
├── src/
│   └── main/
│       ├── java/com/escola/eventos/
│       │   ├── EventosApplication.java       ← Classe principal
│       │   ├── model/
│       │   │   └── Evento.java               ← Entity (tabela do banco) com validações
│       │   ├── repository/
│       │   │   └── EventoDAO.java            ← Repository (JPA)
│       │   └── controller/
│       │       └── WebControl.java           ← Controller (rotas web + tratamento de erros)
│       └── resources/
│           ├── templates/
│           │   ├── eventos.html              ← Tela de listagem (ingressos)
│           │   └── form.html                 ← Tela de cadastro/edição
│           ├── static/css/
│           │   └── style.css                 ← Design system "ticket"
│           └── application.properties        ← Configurações (PostgreSQL via env vars)
├── Dockerfile                                 ← Build multi-estágio (Maven → JRE)
├── docker-compose.yml                        ← Orquestração backend + PostgreSQL + rede
├── .dockerignore
└── pom.xml                                    ← Dependências Maven
```

## Arquitetura dos Containers
```
┌─────────────────────────── eventos-network (bridge) ───────────────────────────┐
│                                                                                  │
│   ┌─────────────────────────┐            ┌─────────────────────────┐          │
│   │   eventos-backend       │            │      eventos-db          │          │
│   │   Spring Boot (Java 17) │  jdbc://   │      PostgreSQL 16       │          │
│   │   porta 8080            ├──────────► │      porta 5432          │          │
│   └────────────┬────────────┘   db:5432  └─────────────────────────┘          │
│                │                                                                │
└────────────────┼────────────────────────────────────────────────────────────────┘
                  │
          http://localhost:8080/eventos
                  │
            Navegador do usuário
```
