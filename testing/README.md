# Testes automatizados — GRUAPIM

Suíte completa para testar a plataforma de ponta a ponta:

- **`gruapim.postman_collection.json`** — coleção Postman com fluxo end-to-end
  (auth → usuários → projetos → membros → user stories → sprints → kanban →
  tasks → git → burndown → atas → notificações → histórico de chat), com
  asserções e encadeamento de variáveis. Roda no Postman **ou** via Newman.
- **`gruapim.postman_environment.json`** — ambiente local (URLs e credenciais semente).
- **`stomp-chat-test.js`** — testa o **chat em tempo real (STOMP/SockJS)** do microsserviço.
- **`run-tests.sh` / `run-tests.ps1`** — rodam tudo (REST + STOMP).

## Pré-requisitos

- Node.js 18+ (testado com 24).
- A aplicação no ar. O mais simples é subir tudo orquestrado, a partir da raiz do repo:

  ```bash
  docker compose up --build
  ```

  Isso sobe `postgres-core`, `postgres-collab`, `rabbitmq`, `gruapim-backend`,
  `collaboration-service` e `gateway`. A coleção aponta para o **gateway** em
  `http://localhost:8090` por padrão.

## Rodar tudo

```bash
cd testing
./run-tests.sh        # Linux/macOS/Git Bash
# ou
./run-tests.ps1       # Windows PowerShell
```

O script instala as dependências na primeira execução e então roda a suíte
REST (Newman) seguida do teste de chat STOMP.

## Rodar separadamente

```bash
cd testing
npm install

# Só REST (Newman):
npm run test:api

# Só chat STOMP:
npm run test:stomp
```

## Apontando para outros endpoints

A coleção usa a variável `baseUrl` (default `http://localhost:8090`, o gateway).
Para rodar direto contra os serviços, sobreponha no Newman:

```bash
newman run gruapim.postman_collection.json -e gruapim.postman_environment.json \
  --env-var baseUrl=http://localhost:8080
```

> Atenção: pelo `baseUrl` do backend (8080) os endpoints de colaboração
> (`/api/chat`, `/api/notifications`, `/api/meeting-notes`) **não existem** —
> eles vivem no microsserviço (8081). Por isso o **gateway (8090)** é o alvo
> recomendado para a suíte completa.

O teste STOMP usa três variáveis de ambiente (todas opcionais):

| Variável | Default | Para quê |
|----------|---------|----------|
| `IDENTITY_URL` | `http://localhost:8080` | login e projetos (backend) |
| `COLLAB_URL` | `http://localhost:8081` | histórico de chat (microsserviço) |
| `WS_URL` | `${COLLAB_URL}/api/ws` | endpoint SockJS/STOMP |

Rodando **tudo pelo gateway**:

```bash
IDENTITY_URL=http://localhost:8090 COLLAB_URL=http://localhost:8090 \
WS_URL=http://localhost:8090/api/ws npm run test:stomp
```

## Como o teste STOMP funciona

1. Faz login e garante um projeto (cria se necessário).
2. Abre a conexão STOMP enviando `Authorization: Bearer <jwt>` no frame **CONNECT**
   — autenticado pelo `StompAuthChannelInterceptor` do microsserviço.
3. Assina `/topic/project/{projectId}/chat` e publica em `/app/chat.send`.
4. Verifica que o **broadcast** chegou (com `senderName` preenchido a partir do JWT)
   e que a mensagem foi **persistida** (consulta o `/history` via REST).

Saída esperada ao final:

```
✓ SUCESSO: chat STOMP autenticado, broadcast e persistência funcionando.
```
