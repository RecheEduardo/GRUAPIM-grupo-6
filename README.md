# GRUAPIM — Arquitetura de Microsserviços

Plataforma de gestão Scrum. O backend monolítico teve o Bounded Context de
**Colaboração e Comunicação** (chat, notificações e atas de reunião) extraído
para um microsserviço independente, seguindo o *Strangler Fig Pattern*.

> Planejamento completo e justificativas: [`PLANEJAMENTO_MICROSERVICO_COLABORACAO.md`](PLANEJAMENTO_MICROSERVICO_COLABORACAO.md).

## Topologia

```
                         ┌──────────────────────────┐
   Cliente Web (React) ──┤   gruapim-gateway :8090   │  (Spring Cloud Gateway)
                         └───────────┬───────────────┘
            /api/chat, /api/notifications, /api/meeting-notes, /ws → collab
            /api/** (restante) → backend
                  ┌──────────────────┴────────────────────┐
       ┌──────────▼───────────┐               ┌────────────▼─────────────┐
       │   gruapim-backend    │               │ collaboration-service     │
       │   :8080 (core)       │               │ :8081 (chat/notif/atas)   │
       │  postgres-core       │               │ postgres-collab           │
       └──────────┬───────────┘               └────────────▲─────────────┘
                  │  publica eventos (task.assigned)        │ consome eventos
                  └──────────►  RabbitMQ (gruapim.events) ───┘  e gera notificações
```

## Serviços

| Serviço | Porta | Banco | Responsabilidade |
|---------|-------|-------|------------------|
| `gruapim-gateway` | 8090 | — | Ponto único de entrada, roteamento por path, CORS, correlation-id |
| `gruapim-backend` | 8080 | `gruapim` | Auth, Projects, Sprints, Kanban, Git (core domain) |
| `gruapim-collaboration-service` | 8081 | `gruapim_collab` | Chat (WebSocket/STOMP), notificações, atas de reunião |

## Decisões de arquitetura

- **Database per service:** cada serviço é dono dos seus dados; sem FK cruzando fronteira.
- **JWT compartilhado:** o microsserviço apenas valida (HS256) tokens emitidos pelo backend.
- **Integração assíncrona:** o backend publica eventos no RabbitMQ; o microsserviço
  consome e gera notificações (com *dead-letter queue*).
- **Resiliência:** *retry* + *circuit breaker* (Resilience4j) no publisher, com fallback
  que apenas loga — o core nunca cai por indisponibilidade do broker.
- **Observabilidade:** Actuator + `/actuator/prometheus` em todos os serviços;
  `X-Correlation-Id` propagado pelo gateway.

## Como rodar (tudo orquestrado)

```bash
docker compose up --build
```

Sobe: `postgres-core`, `postgres-collab`, `rabbitmq`, `gruapim-backend`,
`collaboration-service` e `gateway`. A aplicação fica acessível em
`http://localhost:8090` (RabbitMQ Management em `http://localhost:15672`).
