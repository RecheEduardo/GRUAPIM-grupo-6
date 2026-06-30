# Planejamento — Extração do Microsserviço de Colaboração e Comunicação

> **Disciplina:** Arquitetura de Software / Microsserviços — Grupo 6
> **Projeto base:** GRUAPIM (plataforma de gestão Scrum)
> **Atividade:** Evolução da API com Event Storming → extração estratégica de um Bounded Context para microsserviço isolado.
> **Documento de referência:** `Entrega Event Storming - Grupo 6.pdf`, `DATABASE_ARCHITECTURE.md`, `GRUAPIM_HISTORIAS_DE_USUARIO_GRUPO6.pdf`

---

## 1. Contexto e Motivação

O backend atual (`gruapim-backend`) é um **monólito modular** Spring Boot 3.3 / Java 21,
organizado em camadas (`controller` → `service` → `repository` → `domain`), com um único
banco PostgreSQL e um único deploy.

O Event Storming do Grupo 6 mapeou a linha do tempo do domínio e identificou **6 Bounded
Contexts** (fronteiras lógicas de coesão):

| # | Bounded Context | Responsabilidade |
|---|------------------|------------------|
| 1 | Gestão de Acesso e Identidade | Registro e autenticação |
| 2 | Gestão de Projetos (Workspace) | Criação de projetos e alocação de membros |
| 3 | Planejamento Ágil (Agile Planning) | Ciclo de vida de Sprints e Backlog |
| 4 | Execução e Acompanhamento (Kanban) | Rastreabilidade de tarefas e movimentações no quadro |
| 5 | **Colaboração e Comunicação** | **Mensagens (chat), notificações e notas de reunião** |
| 6 | Integração de Versionamento | Vínculo de commits do Git com o escopo de trabalho |

### Bounded Context escolhido para extração: **Colaboração e Comunicação (Fase 5)**

Conforme a seção *"4. Extração Estratégica"* do documento de Event Storming, a justificativa
para isolar este contexto em um microsserviço independente é:

- **Necessidade de escalabilidade:** o fluxo de comunicação (chat via WebSocket + geração de
  notificações) tem um perfil de consumo de recursos completamente diferente do CRUD HTTP do
  *core domain*. Exige conexões persistentes e alto volume de I/O em tempo real. Isolar permite
  **escalar a mensageria independentemente** da API principal.
- **Criticidade para o negócio:** Kanban e Sprints são o *core domain* (coração do produto). A
  comunicação em tempo real é um **domínio de suporte**. Se o chat cair por sobrecarga, a equipe
  ainda consegue mover cards, finalizar tarefas e consultar o backlog na API original
  (degradação graciosa).
- **Taxa de mudança:** funcionalidades de colaboração mudam rápido (webhooks, menções `@usuario`,
  reações, bots de notificação). Isolar evita recompilar e reiniciar o sistema inteiro a cada
  melhoria no chat.

### Eventos de domínio do contexto (Fase 5 do Event Storming)

```
MensagemEnviadaNoChat   →  Chat (ChatMessage)
NotaDeReuniaoCriada     →  Atas de cerimônias (MeetingNote)
NotificacaoGerada       →  Central de notificações (Notification)
```

---

## 2. Estado Atual (o que será extraído do monólito)

Componentes do monólito que pertencem ao contexto **Colaboração e Comunicação**:

| Camada | Artefatos |
|--------|-----------|
| Domínio | `ChatMessage`, `Notification`, `MeetingNote`, enums `NotificationType`, `MeetingType` |
| Repositório | `ChatMessageRepository`, `NotificationRepository`, `MeetingNoteRepository` |
| Serviço | `ChatService`, `NotificationService`, `MeetingNoteService` |
| Controller | `ChatController` (STOMP + REST), `NotificationController`, `MeetingNoteController` |
| DTO | `SendChatMessageRequest`, `CreateMeetingNoteRequest`, `ChatMessageResponse`, `NotificationResponse`, `MeetingNoteResponse` |
| Infra | `WebSocketConfig`, tabelas `chat_messages`, `notifications`, `meeting_notes` |

**Acoplamento atual (a quebrar):**
- As entidades referenciam `User`, `Project`, `Sprint` via `@ManyToOne` (FK no mesmo banco).
- `chat_messages`, `notifications`, `meeting_notes` têm *foreign keys* físicas para `users`,
  `projects`, `sprints`.
- Observação importante levantada na análise do código: `NotificationService` **ainda não é
  invocado** por outros serviços (Task/Sprint) — hoje só é exposto via REST. Isso **reduz o
  acoplamento de saída** e facilita a extração; a geração de notificações disparada por eventos
  de outros contextos será reconstruída de forma **assíncrona** (commit 5/8).

---

## 3. Arquitetura-Alvo

```
                         ┌──────────────────────────┐
   Cliente Web (React) ──┤      API Gateway          │  (Spring Cloud Gateway — commit 6/8)
                         └───────────┬───────────────┘
                          /api/chat, /api/notifications, /api/meeting-notes
                  ┌──────────────────┴────────────────────┐
                  │                                        │
       ┌──────────▼───────────┐               ┌────────────▼─────────────┐
       │   gruapim-backend    │               │ gruapim-collaboration-   │
       │   (monólito core)    │               │ service (microsserviço)  │
       │  Auth, Projects,     │               │ Chat (WebSocket/STOMP),  │
       │  Sprints, Kanban,Git │               │ Notifications, Meeting   │
       │                      │               │ Notes                    │
       │  PostgreSQL: gruapim │               │ PostgreSQL: gruapim_collab│
       └──────────┬───────────┘               └────────────▲─────────────┘
                  │   publica eventos de domínio            │ consome eventos
                  │   (TaskAssigned, SprintEnded, ...)      │ e gera notificações
                  └──────────────►  Message Broker  ────────┘
                                   (RabbitMQ — commit 5/8)
```

### Princípios de design da extração

| # | Decisão | Justificativa |
|---|---------|---------------|
| 1 | **Banco de dados próprio** (`gruapim_collab`) | *Database per service*. O microsserviço é dono dos seus dados; nada de FK cruzando fronteira. |
| 2 | **Desnormalização de identidade** | Em vez de `@ManyToOne User/Project/Sprint`, persistimos `senderId`/`senderName`, `projectId`, `userId`, `sprintId` como colunas simples (UUID/String). Os dados de referência viajam no evento ou no JWT. |
| 3 | **Segredo JWT compartilhado** | O microsserviço **valida** (não emite) tokens assinados pelo contexto de Identidade. Mesma `HS256 secret` ⇒ confiança sem chamada de rede. |
| 4 | **Integração assíncrona (event-driven)** | A geração de notificações disparada por outros contextos passa a ser feita por mensageria (RabbitMQ), eliminando acoplamento síncrono. |
| 5 | **Strangler Fig Pattern** | O monólito só perde os endpoints de colaboração **depois** que o microsserviço estiver provado e roteado (commit 7/8), evitando *big-bang*. |
| 6 | **Gateway como ponto único** | O cliente não sabe que houve split: o gateway roteia por path-prefix. |

### Stack do microsserviço

- Java 21 + Spring Boot 3.3 (mesma do monólito, reduz curva)
- Spring Web + WebSocket (STOMP) + Spring Data JPA + Flyway + PostgreSQL
- Spring Security (validação JWT) + Actuator
- Spring AMQP (RabbitMQ) — a partir do commit 5/8
- Porta `8081` (monólito permanece em `8080`)

---

## 4. Plano de Implementação — 8 Commits

> Cada commit é **incremental e coeso**: o repositório permanece consistente após cada um.
> Os commits **1 a 4** entregam um microsserviço **funcional e seguro em isolamento**.
> Os commits **5 a 8** completam a **integração, o roteamento e o desligamento do módulo no
> monólito**, resultando numa solução robusta e production-ready.

### ✅ Commit 1/8 — `chore: scaffold do microsserviço de colaboração`
**Objetivo:** Esqueleto do novo módulo, isolado e executável (mesmo que "vazio").

- Criar módulo `gruapim-collaboration-service/` (sibling de `gruapim-backend/`).
- `pom.xml` (Spring Boot 3.3, Java 21, dependências web/jpa/websocket/security/flyway/actuator/postgres).
- `CollaborationServiceApplication.java` (`@SpringBootApplication`, auditing JPA).
- `application.yml` (porta 8081, context-path `/api`), `application-dev.yml` (DB `gruapim_collab`),
  `application-prod.yml` (variáveis de ambiente).
- `Dockerfile` multi-stage (build + JRE alpine).
- `.gitignore`, `README.md` do serviço.
- Este documento de planejamento.

**Resultado:** `mvn spring-boot:run` sobe um serviço Spring Boot vazio na porta 8081 com `/actuator/health`.

---

### ✅ Commit 2/8 — `feat: domínio e persistência do contexto de colaboração`
**Objetivo:** Modelo de dados próprio, **sem FK cruzando fronteira de serviço**.

- Enums `NotificationType`, `MeetingType`.
- `BaseEntity` (id UUID + auditoria `createdAt`/`updatedAt`).
- Entidades **desnormalizadas**:
  - `ChatMessage` → `id, projectId, senderId, senderName, content, sentAt`
  - `Notification` → `id, userId, type, title, message, read, relatedEntityType, relatedEntityId, createdAt`
  - `MeetingNote` → `id, sprintId, projectId, type, discussedPoints, decisions, improvementActions, createdById, createdByName, createdAt, updatedAt`
- Repositórios Spring Data (`ChatMessageRepository`, `NotificationRepository`, `MeetingNoteRepository`).
- Migrations Flyway próprias: `V1__create_chat_messages.sql`, `V2__create_notifications.sql`,
  `V3__create_meeting_notes.sql` — **sem** `REFERENCES users/projects/sprints`.

**Resultado:** Schema do microsserviço versionado e isolado; Hibernate valida contra o Flyway.

---

### ✅ Commit 3/8 — `feat: serviços, API REST e WebSocket de colaboração`
**Objetivo:** Tornar o serviço **funcionalmente completo em isolamento**.

- DTOs request/response adaptados ao modelo desnormalizado.
- `security/UserPrincipal` (record `id/email/name`) — identidade do chamador (populada no commit 4).
- Serviços:
  - `ChatService.send(...)` persiste e publica em `/topic/project/{id}/chat` via `SimpMessagingTemplate`.
  - `NotificationService` (listar, não-lidas, marcar lida/todas, criar+push `/queue/notifications`).
  - `MeetingNoteService` (criar, listar por sprint, buscar, excluir).
- Controllers: `ChatController` (STOMP `/app/chat.send` + REST histórico), `NotificationController`,
  `MeetingNoteController`.
- `WebSocketConfig` (broker `/topic` e `/queue`, endpoint `/ws` + SockJS).
- `GlobalExceptionHandler` + exceções de domínio.

**Resultado:** Microsserviço atende chat, notificações e atas de ponta a ponta (ainda sem auth real).

---

### ✅ Commit 4/8 — `feat: segurança JWT entre serviços (segredo compartilhado)`
**Objetivo:** Confiança *stateless* entre o contexto de Identidade e o microsserviço.

- `JwtService` (valida HS256 com segredo compartilhado, extrai `sub`, `uid`, `name`, roles).
- `JwtAuthFilter` (constrói `UserPrincipal` a partir das claims; sem tabela de usuários local).
- `SecurityConfig` (stateless, paths públicos: actuator/health, swagger, `/ws`).
- **Mudança no monólito:** enriquecer o token emitido (`AuthService`/`JwtService`) com claims
  `uid` (UUID do usuário) e `name`, para que o microsserviço preencha `senderName`/`createdByName`
  sem consultar o serviço de Identidade.

**Resultado:** Endpoints do microsserviço protegidos; identidade flui via JWT assinado pelo monólito.
**→ Marco: microsserviço funcional, seguro e independente.**

---

### ⏳ Commit 5/8 — `feat: integração assíncrona via RabbitMQ (event-driven)`
**Objetivo:** Substituir acoplamento síncrono por **eventos de domínio**.

- Adicionar `spring-boot-starter-amqp` em ambos os serviços.
- Definir contrato de eventos (`TaskAssignedEvent`, `SprintEndingEvent`, `CommentAddedEvent`,
  `MemberAddedEvent`) num pacote de contrato compartilhado/duplicado.
- **Monólito (publisher):** publicar eventos no exchange `gruapim.events` quando ocorrer a ação
  (ex.: tarefa atribuída em `TaskService`).
- **Microsserviço (consumer):** `@RabbitListener` que consome os eventos e cria a notificação
  correspondente (substitui a antiga chamada direta a `NotificationService`).
- Configuração de exchange/queue/binding e *dead-letter queue*.

**Resultado:** Geração de notificações desacoplada; produtor e consumidor evoluem independentes.

---

### ⏳ Commit 6/8 — `feat: API Gateway e roteamento por contexto`
**Objetivo:** Ponto único de entrada; cliente não percebe o split.

- Novo módulo `gruapim-gateway` (Spring Cloud Gateway) **ou** configuração de reverse-proxy.
- Rotas:
  - `/api/chat/**`, `/api/notifications/**`, `/api/meeting-notes/**`, `/ws/**` → `collaboration-service:8081`
  - `/api/**` (restante) → `gruapim-backend:8080`
- Repasse do header `Authorization` (JWT) para os serviços downstream.
- CORS centralizado no gateway.

**Resultado:** Uma URL pública; roteamento transparente por path-prefix.

---

### ⏳ Commit 7/8 — `refactor: remover módulo de colaboração do monólito (strangler)`
**Objetivo:** Concluir o *Strangler Fig* — o monólito para de servir o contexto extraído.

- Remover do `gruapim-backend`: controllers/services/repos/entidades/DTOs de chat, notificação e atas.
- Remover `WebSocketConfig` e a dependência `spring-boot-starter-websocket` se não usada em outro lugar.
- Migration no monólito para **descontinuar** a escrita nessas tabelas (mantendo histórico ou
  migrando dados para o `gruapim_collab` via script de *data migration* documentado).
- Onde o monólito precisava notificar, passa a **publicar evento** (do commit 5/8) em vez de chamar
  serviço local.

**Resultado:** Responsabilidade 100% transferida; monólito mais enxuto e coeso.

---

### ⏳ Commit 8/8 — `chore: orquestração, observabilidade e resiliência`
**Objetivo:** Solução completa, robusta e production-ready.

- `docker-compose.yml` orquestrando: `postgres-core`, `postgres-collab`, `rabbitmq`,
  `gruapim-backend`, `collaboration-service`, `gateway`.
- *Health checks*, `depends_on`, redes e volumes.
- Resiliência: *retry* + *circuit breaker* (Resilience4j) no publisher de eventos; DLQ no consumer.
- Observabilidade: Actuator + métricas (Prometheus), *correlation-id* propagado pelo gateway.
- Testes de integração (Testcontainers: Postgres + RabbitMQ) do fluxo chat→persistência e
  evento→notificação.
- README geral da arquitetura de microsserviços + diagrama final.

**Resultado:** Sistema multi-serviço orquestrado, resiliente, observável e testado — extração
do Bounded Context **Colaboração e Comunicação** concluída.

---

## 5. Matriz de Rastreabilidade (Event Storming → Commit)

| Evento / Decisão do Event Storming | Onde é atendido |
|------------------------------------|-----------------|
| `MensagemEnviadaNoChat` | Commits 2–4 (entidade/serviço/REST/WebSocket + auth) |
| `NotaDeReuniaoCriada` | Commits 2–4 |
| `NotificacaoGerada` (sob demanda) | Commits 2–4 |
| `NotificacaoGerada` (disparada por outros contextos) | Commit 5 (event-driven) |
| Escalabilidade independente | Commits 1 (deploy próprio), 5 (mensageria), 8 (orquestração) |
| Degradação graciosa / criticidade | Commits 6 (gateway), 8 (resiliência/DLQ) |
| Database per service | Commit 2 |
| Confiança entre serviços | Commit 4 (JWT compartilhado) |
| Transparência para o cliente | Commit 6 (gateway) |
| Conclusão do *strangler* | Commit 7 |

---

## 6. Escopo desta entrega

- **Commits 1 a 4 implementados** nesta iteração (microsserviço funcional, seguro e independente).
- **Commits 5 a 8 planejados** e detalhados acima, a serem implementados nas próximas iterações
  até a solução final robusta.
