# gruapim-collaboration-service

Microsserviço de **Colaboração e Comunicação** do GRUAPIM, extraído do monólito
`gruapim-backend` conforme o Event Storming do Grupo 6 (Bounded Context da Fase 5).

Responsável por três capacidades em tempo real:

- **Chat** por projeto (WebSocket/STOMP + histórico persistido)
- **Central de notificações** por usuário (push via WebSocket)
- **Atas de reunião** (Review / Retrospectiva) por sprint

## Por que um serviço separado?

| Critério | Razão |
|----------|-------|
| Escalabilidade | Chat (conexões persistentes / alto I/O) escala independente do CRUD do core |
| Criticidade | Domínio de suporte — se cair, o core (Kanban/Sprints) continua operando |
| Taxa de mudança | Colaboração evolui rápido (menções, reações, bots) sem recompilar o monólito |

## Características arquiteturais

- **Database per service:** banco próprio `gruapim_collab` (sem FK cruzando fronteira).
- **Identidade desnormalizada:** `senderId/senderName`, `userId`, `projectId`, `sprintId`
  como colunas simples; dados de referência chegam pelo JWT ou por eventos.
- **Confiança stateless:** valida JWT HS256 assinado pelo serviço de Identidade (segredo
  compartilhado) — não emite tokens.

## Stack

Java 21 · Spring Boot 3.3 · Spring Web/WebSocket/Security/Data JPA · Flyway · PostgreSQL · Actuator

## Executando localmente

```bash
# Banco (exemplo)
createdb gruapim_collab

# Subir o serviço (perfil dev, porta 8081)
mvn spring-boot:run
```

Health check: `GET http://localhost:8081/api/actuator/health`

## Portas

| Serviço | Porta |
|---------|-------|
| gruapim-backend (monólito core) | 8080 |
| gruapim-collaboration-service | 8081 |

## Roadmap de extração (ver `../PLANEJAMENTO_MICROSERVICO_COLABORACAO.md`)

1. ✅ Scaffolding
2. ⏳ Domínio e persistência
3. ⏳ Serviços, REST e WebSocket
4. ⏳ Segurança JWT entre serviços
5. ⏳ Integração assíncrona (RabbitMQ)
6. ⏳ API Gateway
7. ⏳ Remoção do módulo no monólito (strangler)
8. ⏳ Orquestração, observabilidade e resiliência
