#!/usr/bin/env bash
# Roda a suíte REST (Newman) e em seguida o teste de chat STOMP.
set -e
cd "$(dirname "$0")"

if [ ! -d node_modules ]; then
  echo ">> Instalando dependências (newman, @stomp/stompjs, sockjs-client)..."
  npm install
fi

echo ">> Suíte REST (Newman)..."
npm run test:api

echo ">> Teste de chat STOMP..."
npm run test:stomp

echo ">> Todos os testes passaram."
