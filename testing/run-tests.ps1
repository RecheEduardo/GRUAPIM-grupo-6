# Roda a suíte REST (Newman) e em seguida o teste de chat STOMP.
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

if (-not (Test-Path node_modules)) {
    Write-Host ">> Instalando dependências (newman, @stomp/stompjs, sockjs-client)..."
    npm install
}

Write-Host ">> Suíte REST (Newman)..."
npm run test:api
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ">> Teste de chat STOMP..."
npm run test:stomp
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ">> Todos os testes passaram."
