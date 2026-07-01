/**
 * Teste automatizado do chat em tempo real (STOMP sobre SockJS) do
 * microsserviço de Colaboração.
 *
 * Fluxo:
 *   1. Login (serviço de Identidade) -> obtém JWT.
 *   2. Garante um projeto (cria se necessário) -> projectId.
 *   3. Abre conexão STOMP autenticada (Authorization no frame CONNECT).
 *   4. Assina /topic/project/{id}/chat e publica em /app/chat.send.
 *   5. Confirma o recebimento via broadcast e a persistência via REST /history.
 *
 * Endpoints (sobrescreva por variáveis de ambiente):
 *   IDENTITY_URL  login/projetos   (default http://localhost:8080 — backend)
 *   COLLAB_URL    chat REST + ws   (default http://localhost:8081 — microsserviço)
 *   WS_URL        endpoint SockJS  (default COLLAB_URL + /api/ws)
 *
 * Para rodar tudo via gateway:
 *   IDENTITY_URL=http://localhost:8090 COLLAB_URL=http://localhost:8090 \
 *   WS_URL=http://localhost:8090/api/ws node stomp-chat-test.js
 */
const { Client } = require('@stomp/stompjs');
const SockJS = require('sockjs-client');

const IDENTITY_URL = process.env.IDENTITY_URL || 'http://localhost:8080';
const COLLAB_URL = process.env.COLLAB_URL || 'http://localhost:8081';
const WS_URL = process.env.WS_URL || `${COLLAB_URL}/api/ws`;

const EMAIL = process.env.ALICE_EMAIL || 'alice@gruapim.dev';
const PASSWORD = process.env.ALICE_PASSWORD || 'alice12345';
const NAME = process.env.ALICE_NAME || 'Alice Scrum';
const TIMEOUT_MS = Number(process.env.STOMP_TIMEOUT_MS || 12000);

function log(step, msg) { console.log(`  [${step}] ${msg}`); }
function fail(msg) { console.error(`\n✗ FALHOU: ${msg}`); process.exit(1); }

async function http(method, url, token, body) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers.Authorization = `Bearer ${token}`;
  const res = await fetch(url, { method, headers, body: body ? JSON.stringify(body) : undefined });
  const text = await res.text();
  const json = text ? JSON.parse(text) : null;
  if (!res.ok) throw new Error(`${method} ${url} -> ${res.status}: ${text}`);
  return json;
}

async function login() {
  // registra (idempotente) e faz login
  try {
    await http('POST', `${IDENTITY_URL}/api/auth/register`, null,
      { name: NAME, email: EMAIL, password: PASSWORD, role: 'SCRUM_MASTER' });
  } catch (_) { /* já existe — segue para o login */ }
  const auth = await http('POST', `${IDENTITY_URL}/api/auth/login`, null,
    { email: EMAIL, password: PASSWORD });
  return auth.token;
}

async function ensureProject(token) {
  const projects = await http('GET', `${IDENTITY_URL}/api/projects`, token);
  if (Array.isArray(projects) && projects.length > 0) return projects[0].id;
  const created = await http('POST', `${IDENTITY_URL}/api/projects`, token,
    { name: 'Projeto STOMP', description: 'Criado pelo teste de chat' });
  return created.id;
}

function connectAndExchange(token, projectId) {
  return new Promise((resolve, reject) => {
    const content = `Mensagem de teste STOMP ${Date.now()}`;
    const timer = setTimeout(
      () => reject(new Error(`nenhuma mensagem recebida em ${TIMEOUT_MS}ms`)), TIMEOUT_MS);

    const client = new Client({
      webSocketFactory: () => new SockJS(WS_URL),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 0,
      debug: () => {},
    });

    client.onStompError = (frame) =>
      reject(new Error(`STOMP error: ${frame.headers['message']} ${frame.body || ''}`));
    client.onWebSocketError = (e) =>
      reject(new Error(`WebSocket error: ${e && e.message ? e.message : e}`));

    client.onConnect = () => {
      log('stomp', 'CONNECT autenticado, assinando /topic...');
      client.subscribe(`/topic/project/${projectId}/chat`, (message) => {
        clearTimeout(timer);
        let payload;
        try { payload = JSON.parse(message.body); } catch (e) { return reject(e); }
        client.deactivate();
        resolve({ content, received: payload });
      });
      // pequena folga para a assinatura propagar antes de publicar
      setTimeout(() => {
        log('stomp', `publicando em /app/chat.send: "${content}"`);
        client.publish({
          destination: '/app/chat.send',
          body: JSON.stringify({ projectId, content }),
        });
      }, 300);
    };

    client.activate();
  });
}

(async () => {
  console.log('\n=== Teste STOMP do chat (microsserviço de colaboração) ===');
  console.log(`  identidade: ${IDENTITY_URL} | colaboração: ${COLLAB_URL} | ws: ${WS_URL}\n`);
  try {
    const token = await login();
    log('auth', 'login OK, JWT obtido');

    const projectId = await ensureProject(token);
    log('setup', `projectId = ${projectId}`);

    const { content, received } = await connectAndExchange(token, projectId);

    if (received.content !== content) {
      fail(`conteúdo divergente. esperado="${content}" recebido="${received.content}"`);
    }
    log('assert', `broadcast recebido: senderName="${received.senderName}" content OK`);
    if (!received.senderName) {
      fail('senderName vazio — a autenticação STOMP não preencheu o principal');
    }

    // confirma persistência via REST
    const history = await http('GET',
      `${COLLAB_URL}/api/chat/project/${projectId}/history?limit=50`, token);
    const persisted = Array.isArray(history) && history.some((m) => m.content === content);
    if (!persisted) fail('mensagem não encontrada no histórico (persistência)');
    log('assert', 'mensagem persistida e retornada pelo /history');

    console.log('\n✓ SUCESSO: chat STOMP autenticado, broadcast e persistência funcionando.\n');
    process.exit(0);
  } catch (err) {
    fail(err.message);
  }
})();
