# GRUAPIM — Guia de Apresentação

> Documento de apoio para apresentar o projeto. Linguagem direta, do "porquê" ao "como".
> Se você ler só este arquivo, já consegue apresentar com segurança.

---

## 1. O que é o GRUAPIM (a frase de abertura)

> **GRUAPIM é uma plataforma de gestão de projetos no estilo Scrum** — como um Trello/Jira
> simplificado. A equipe cria projetos, escreve histórias de usuário, organiza sprints,
> move tarefas num quadro Kanban, conversa por chat, recebe notificações e registra atas
> de reunião.

O foco do trabalho **não é o produto em si**, e sim a **arquitetura**: como o sistema
foi reorganizado de um **monólito** (um bloco único) para **microsserviços**
(blocos independentes que conversam entre si).

---

## 2. A história central (o "fio" da apresentação)

Esta é a mensagem mais importante. Decore esta narrativa:

> "O sistema começou como um **monólito**: tudo num programa só, com um banco de dados só.
> Funcionava, mas tinha um problema: a parte de **comunicação em tempo real** (chat e
> notificações) tem necessidades muito diferentes do resto. Então **separamos essa parte
> em um serviço independente**, sem derrubar o sistema, usando uma técnica chamada
> **Strangler Fig** (estrangulamento gradual). Hoje temos **3 serviços** que conversam
> de forma segura e desacoplada."

**Por que separar justamente o chat/notificações?** (a justificativa que impressiona)
- **Escalabilidade diferente:** chat usa conexões abertas o tempo todo (tempo real); o
  resto é só requisição-resposta normal. Separar permite "reforçar" só o chat quando precisar.
- **Criticidade diferente:** Kanban e Sprints são o **coração** do produto. Chat é **apoio**.
  Se o chat cair, a equipe ainda move cards e fecha tarefas normalmente → **degradação graciosa**.
- **Muda muito:** funcionalidades de chat mudam toda hora (menções, reações...). Isolar
  evita ter que recompilar e reiniciar o sistema inteiro a cada ajuste no chat.

---

## 3. Os 3 serviços (o "mapa" do sistema)

| Serviço | Porta | O que faz | Banco próprio |
|---------|-------|-----------|---------------|
| **Gateway** | 8090 | **Porta de entrada única.** Recebe tudo do cliente e direciona para o serviço certo. | — |
| **Backend (core)** | 8080 | O coração: **login, projetos, sprints, Kanban, integração com Git**. | `gruapim` |
| **Collaboration Service** | 8081 | A parte extraída: **chat, notificações e atas de reunião**. | `gruapim_collab` |

```
   Cliente Web (React)
            │
            ▼
   ┌──────────────────┐
   │     GATEWAY      │  ← porta 8090, ponto único de entrada
   │   (porta 8090)   │
   └────────┬─────────┘
            │  decide para onde mandar, pelo endereço (path):
            │  /api/chat, /api/notifications, /api/meeting-notes  → Collaboration
            │  /api/...(todo o resto)                             → Backend
     ┌──────┴────────────────────────┐
     ▼                                ▼
┌──────────────┐              ┌────────────────────┐
│   BACKEND    │              │  COLLABORATION     │
│  (porta 8080)│              │   (porta 8081)     │
│ Login, Proj, │              │ Chat, Notificações,│
│ Sprint, Kanban│             │ Atas de reunião    │
│  Banco: gruapim│            │ Banco: gruapim_collab│
└──────┬───────┘              └─────────▲──────────┘
       │   publica "avisos" (eventos)   │  escuta os avisos e
       └──────►  RabbitMQ (correio)  ────┘  gera notificações
```

---

## 4. Como os serviços conversam (3 mecanismos)

Há **três formas** de comunicação. Saber distingui-las é o ponto técnico mais forte.

### a) Gateway — a "recepção" do prédio
O cliente (site React) só conhece **um endereço**: o gateway (porta 8090). Ele olha o
endereço da requisição e encaminha para o serviço correto. **O cliente nem sabe que existem
3 serviços** — para ele parece um sistema só. O gateway também cuida de CORS e adiciona um
`X-Correlation-Id` (um "número de protocolo") para rastrear cada requisição entre serviços.

### b) JWT compartilhado — o "crachá" de confiança
- O **Backend** faz o login e emite um **token JWT** (um crachá digital assinado).
- O **Collaboration Service** **não refaz o login**; ele só **confere a assinatura** do crachá
  usando o **mesmo segredo**. Se a assinatura bate, ele confia.
- Vantagem: **não precisa fazer chamada de rede** para validar o usuário. O próprio token já
  carrega quem é a pessoa (id, nome) → mais rápido e desacoplado.

### c) RabbitMQ — o "correio" assíncrono (event-driven)
Esta é a parte mais sofisticada. Quando algo acontece no Backend (ex.: **uma tarefa é
atribuída a alguém**), o Backend **não liga diretamente** para o Collaboration Service.
Em vez disso:
1. O Backend **publica um evento** (uma "carta": *"tarefa X foi atribuída ao fulano"*) numa
   fila central, o **RabbitMQ**.
2. O Collaboration Service **escuta** essa fila e, ao receber a carta, **cria a notificação**.

**Por que isso é melhor que ligar direto?**
- Os dois serviços ficam **desacoplados**: o Backend não precisa saber se o Collaboration
  está ligado ou não. Ele só "posta a carta" e segue a vida.
- Se o Collaboration estiver fora do ar, as cartas **ficam guardadas** na fila e são
  processadas quando ele voltar.

---

## 5. Os 5 conceitos para "vender" a arquitetura

Se perguntarem "o que tem de bom aqui?", estes são os pontos:

| Conceito | Em uma frase |
|----------|--------------|
| **Strangler Fig Pattern** | Tiramos o chat do monólito **aos poucos**, sem parar o sistema (sem "big bang"). |
| **Database per service** | Cada serviço tem **seu próprio banco**; nenhum mexe na tabela do outro. Isso garante independência real. |
| **JWT compartilhado** | Confiança entre serviços **sem refazer login** — só conferindo a assinatura do token. |
| **Event-driven (RabbitMQ)** | Serviços se avisam por **eventos/filas**, não por chamadas diretas → desacoplamento. |
| **Resiliência + Observabilidade** | Se o "correio" falha, o sistema **não cai** (retry + circuit breaker). E tudo é **monitorável** (métricas Prometheus, correlation-id). |

**Sobre Resiliência (detalhe que vale citar):** o Backend usa **retry** (tenta de novo) e
**circuit breaker** (desarma se o correio estiver fora) com fallback que só registra um log.
Ou seja: **o coração do sistema nunca cai** porque o RabbitMQ está indisponível.

---

## 6. Tecnologias (a "ficha técnica")

- **Linguagem:** Java 21 · **Framework:** Spring Boot 3.3
- **Gateway:** Spring Cloud Gateway
- **Banco:** PostgreSQL (um por serviço) · **Migrations:** Flyway
- **Mensageria:** RabbitMQ (Spring AMQP)
- **Tempo real:** WebSocket / STOMP (para o chat)
- **Segurança:** Spring Security + JWT (HS256)
- **Resiliência:** Resilience4j (retry + circuit breaker)
- **Observabilidade:** Spring Actuator + Prometheus
- **Orquestração:** Docker Compose (sobe tudo com um comando)

---

## 7. Demonstração / como rodar (se for ao vivo)

```bash
docker compose up --build
```

Isso sobe **tudo de uma vez**: os 2 bancos PostgreSQL, o RabbitMQ e os 3 serviços.
- Aplicação: **http://localhost:8090**
- Painel do RabbitMQ (ver as "cartas" passando): **http://localhost:15672**

**Roteiro de demo sugerido:** fazer login → criar/mover uma tarefa → mostrar a notificação
aparecendo (gerada via RabbitMQ) → abrir o chat (tempo real via WebSocket).

---

## 8. Perguntas prováveis da banca (e respostas curtas)

**"Por que não deixaram tudo monólito?"**
> Para escalar e evoluir a comunicação em tempo real de forma independente do core, e para
> que uma falha no chat não derrube o produto principal.

**"Por que só extraíram um serviço, e não tudo?"**
> Estratégia Strangler Fig: extração **gradual e segura**. Começa-se pelo contexto de maior
> ganho (comunicação) e prova-se o modelo antes de continuar.

**"Como um serviço confia no outro sem refazer login?"**
> JWT com segredo compartilhado: o Backend assina o token, o Collaboration só valida a
> assinatura. Mesmo segredo = confiança, sem chamada de rede.

**"E se o RabbitMQ cair?"**
> O Backend tem retry + circuit breaker; ele não trava. O core continua funcionando e o
> envio de notificações degrada de forma graciosa.

**"Os serviços compartilham banco?"**
> Não. **Database per service.** Cada um é dono dos seus dados; não há chave estrangeira
> cruzando a fronteira entre serviços.

**"Como o cliente lida com 3 serviços?"**
> Ele não lida — só fala com o **Gateway** (uma URL). O roteamento é transparente.

---

## 9. Domínio detalhado (caso queira aprofundar)

O monólito mapeou **6 contextos** (áreas) no Event Storming:
1. **Identidade** — registro e login
2. **Projetos** — criação de projetos e membros
3. **Planejamento Ágil** — sprints e backlog
4. **Kanban** — quadro e tarefas
5. **Colaboração** — chat, notificações, atas ← **este foi extraído**
6. **Git** — vínculo de commits com tarefas

Principais entidades: `users`, `projects`, `sprints`, `user_stories`, `tasks`,
`kanban_columns`, `chat_messages`, `notifications`, `meeting_notes`, `git_repositories`.
(Detalhe completo em [`DATABASE_ARCHITECTURE.md`](DATABASE_ARCHITECTURE.md).)

---

### Documentos relacionados
- [`README.md`](README.md) — visão geral e topologia
- [`PLANEJAMENTO_MICROSERVICO_COLABORACAO.md`](PLANEJAMENTO_MICROSERVICO_COLABORACAO.md) — o porquê e o passo a passo da extração (8 commits)
- [`DATABASE_ARCHITECTURE.md`](DATABASE_ARCHITECTURE.md) — modelo de dados completo
