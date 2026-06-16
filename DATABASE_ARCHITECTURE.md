# GRUAPIM — Arquitetura do Banco de Dados

**SGBD:** PostgreSQL 15+  
**Migrações:** Flyway (`src/main/resources/db/migration/`)  
**Chaves primárias:** UUID v4 gerado pelo Hibernate  
**Auditoria:** `created_at` / `updated_at` via Spring Data JPA `@EnableJpaAuditing`

---

## Mapa de Entidades

```
users
 ├─< project_members >── projects
 ├─< user_stories       (created_by)
 ├─< sprints            (created_by)
 ├─< tasks              (created_by / assignee)
 ├─< task_status_history (changed_by)
 ├─< meeting_notes      (created_by)
 ├─< chat_messages      (sender)
 ├─< story_comments     (author)
 ├─< notifications
 └─< git_repositories   (connected_by)

projects
 ├─< project_members
 ├─< user_stories
 ├─< sprints
 ├─< kanban_columns
 ├─< chat_messages
 └─< git_repositories

user_stories
 ├─< sprint_stories >── sprints
 ├─< tasks
 └─< story_comments

sprints
 ├─< sprint_stories
 └─< meeting_notes

tasks
 ├─< task_status_history
 ├─< git_commit_links
 └── kanban_column (FK)

git_repositories
 └─< git_commit_links
```

---

## Tabelas

### `users`
| Coluna      | Tipo                  | Restrições                                                   |
|-------------|----------------------|--------------------------------------------------------------|
| id          | UUID                 | PK, NOT NULL                                                 |
| name        | VARCHAR(255)         | NOT NULL                                                     |
| email       | VARCHAR(255)         | NOT NULL, UNIQUE                                             |
| password    | VARCHAR(255)         | NOT NULL (bcrypt hash)                                       |
| role        | VARCHAR(50)          | NOT NULL — `ADMIN \| PRODUCT_OWNER \| SCRUM_MASTER \| DEVELOPER` |
| active      | BOOLEAN              | NOT NULL, DEFAULT TRUE                                       |
| created_at  | TIMESTAMPTZ          | NOT NULL, imutável                                           |
| updated_at  | TIMESTAMPTZ          | NOT NULL                                                     |

**Índices:** `idx_users_email` (UNIQUE)

> `role` aqui é o papel **global** do usuário. O papel por projeto fica em `project_members`.

---

### `projects`
| Coluna        | Tipo         | Restrições                         |
|---------------|--------------|------------------------------------|
| id            | UUID         | PK, NOT NULL                       |
| name          | VARCHAR(255) | NOT NULL                           |
| description   | TEXT         |                                    |
| created_by_id | UUID         | FK → users.id, NOT NULL            |
| created_at    | TIMESTAMPTZ  | NOT NULL, imutável                 |
| updated_at    | TIMESTAMPTZ  | NOT NULL                           |

**Índices:** `idx_projects_created_by`

---

### `project_members`
Associa usuários a projetos com papéis específicos no contexto do projeto.

| Coluna     | Tipo        | Restrições                                               |
|------------|-------------|----------------------------------------------------------|
| id         | UUID        | PK, NOT NULL                                             |
| project_id | UUID        | FK → projects.id, NOT NULL                               |
| user_id    | UUID        | FK → users.id, NOT NULL                                  |
| role       | VARCHAR(50) | NOT NULL — `PRODUCT_OWNER \| SCRUM_MASTER \| DEVELOPER`  |
| joined_at  | TIMESTAMPTZ | NOT NULL, imutável                                       |

**UNIQUE:** `(project_id, user_id)`  
**Índices:** `idx_pm_project_id`, `idx_pm_user_id`

---

### `user_stories`
Histórias de usuário do backlog do produto.

| Coluna         | Tipo         | Restrições                                              |
|----------------|--------------|---------------------------------------------------------|
| id             | UUID         | PK, NOT NULL                                            |
| project_id     | UUID         | FK → projects.id, NOT NULL                              |
| title          | VARCHAR(255) | NOT NULL                                                |
| description    | TEXT         |                                                         |
| priority       | VARCHAR(20)  | NOT NULL — `HIGH \| MEDIUM \| LOW`                      |
| status         | VARCHAR(30)  | NOT NULL — `BACKLOG \| IN_SPRINT \| IN_PROGRESS \| DONE` |
| story_points   | INTEGER      |                                                         |
| position_order | INTEGER      | NOT NULL, DEFAULT 0 (ordena drag-and-drop no backlog)   |
| created_by_id  | UUID         | FK → users.id, NOT NULL                                 |
| created_at     | TIMESTAMPTZ  | NOT NULL, imutável                                      |
| updated_at     | TIMESTAMPTZ  | NOT NULL                                                |

**Índices:** `idx_us_project_id`, `idx_us_status`, `idx_us_priority`

---

### `sprints`
Iterações temporais (ciclos) do Scrum.

| Coluna        | Tipo         | Restrições                                             |
|---------------|--------------|--------------------------------------------------------|
| id            | UUID         | PK, NOT NULL                                           |
| project_id    | UUID         | FK → projects.id, NOT NULL                             |
| name          | VARCHAR(255) | NOT NULL                                               |
| goal          | TEXT         |                                                        |
| start_date    | DATE         | NOT NULL                                               |
| end_date      | DATE         | NOT NULL                                               |
| status        | VARCHAR(30)  | NOT NULL — `PLANNED \| IN_PROGRESS \| COMPLETED`       |
| created_by_id | UUID         | FK → users.id, NOT NULL                                |
| created_at    | TIMESTAMPTZ  | NOT NULL, imutável                                     |
| updated_at    | TIMESTAMPTZ  | NOT NULL                                               |

**Índices:** `idx_sprints_project_id`, `idx_sprints_status`

> A regra de não sobreposição de datas entre sprints ativas é validada na camada de serviço.

---

### `sprint_stories`
Alocação de histórias em sprints (relação N:N gerenciada explicitamente).

| Coluna     | Tipo        | Restrições                          |
|------------|-------------|-------------------------------------|
| id         | UUID        | PK, NOT NULL                        |
| sprint_id  | UUID        | FK → sprints.id, NOT NULL           |
| story_id   | UUID        | FK → user_stories.id, NOT NULL      |
| added_at   | TIMESTAMPTZ | NOT NULL, imutável                  |

**UNIQUE:** `(sprint_id, story_id)`  
**Índices:** `idx_ss_sprint_id`, `idx_ss_story_id`

---

### `kanban_columns`
Colunas customizáveis do quadro Kanban, configuradas por projeto.

| Coluna     | Tipo         | Restrições                         |
|------------|--------------|------------------------------------|
| id         | UUID         | PK, NOT NULL                       |
| project_id | UUID         | FK → projects.id, NOT NULL         |
| name       | VARCHAR(100) | NOT NULL                           |
| position   | INTEGER      | NOT NULL (ordem das colunas)       |
| terminal   | BOOLEAN      | NOT NULL, DEFAULT FALSE            |
| created_at | TIMESTAMPTZ  | NOT NULL, imutável                 |
| updated_at | TIMESTAMPTZ  | NOT NULL                           |

**Índices:** `idx_kc_project_id`

> `terminal = TRUE` marca a coluna "Concluído". Uma coluna terminal não pode ser removida se contiver tarefas. Usada no cálculo do burndown sem hardcode de status.

---

### `tasks`
Tarefas técnicas vinculadas a histórias de usuário.

| Coluna           | Tipo         | Restrições                                    |
|------------------|--------------|-----------------------------------------------|
| id               | UUID         | PK, NOT NULL                                  |
| story_id         | UUID         | FK → user_stories.id, NOT NULL                |
| title            | VARCHAR(255) | NOT NULL                                      |
| description      | TEXT         |                                               |
| assignee_id      | UUID         | FK → users.id, NULLABLE                       |
| status           | VARCHAR(30)  | NOT NULL — `TODO \| IN_PROGRESS \| DONE`      |
| kanban_column_id | UUID         | FK → kanban_columns.id, NULLABLE              |
| created_by_id    | UUID         | FK → users.id, NOT NULL                       |
| created_at       | TIMESTAMPTZ  | NOT NULL, imutável                            |
| updated_at       | TIMESTAMPTZ  | NOT NULL                                      |

**Índices:** `idx_tasks_story_id`, `idx_tasks_assignee_id`, `idx_tasks_status`

> `status` é a representação lógica (alimenta burndown). `kanban_column_id` é a posição visual no quadro. Mover a tarefa para uma coluna `terminal` automaticamente seta `status = DONE`.

---

### `task_status_history`
Histórico de mudanças de status de tarefas (auditoria / HU08).

| Coluna        | Tipo        | Restrições                          |
|---------------|-------------|-------------------------------------|
| id            | UUID        | PK, NOT NULL                        |
| task_id       | UUID        | FK → tasks.id, NOT NULL             |
| old_status    | VARCHAR(30) | NULLABLE (registro inicial)         |
| new_status    | VARCHAR(30) | NOT NULL                            |
| changed_by_id | UUID        | FK → users.id, NOT NULL             |
| changed_at    | TIMESTAMPTZ | NOT NULL, imutável                  |

**Índices:** `idx_tsh_task_id`

---

### `meeting_notes`
Atas de reuniões (Review / Retrospectiva) vinculadas a sprints (HU09).

| Coluna              | Tipo        | Restrições                             |
|---------------------|-------------|----------------------------------------|
| id                  | UUID        | PK, NOT NULL                           |
| sprint_id           | UUID        | FK → sprints.id, NOT NULL              |
| type                | VARCHAR(30) | NOT NULL — `REVIEW \| RETROSPECTIVE`   |
| discussed_points    | TEXT        |                                        |
| decisions           | TEXT        |                                        |
| improvement_actions | TEXT        |                                        |
| created_by_id       | UUID        | FK → users.id, NOT NULL                |
| created_at          | TIMESTAMPTZ | NOT NULL, imutável                     |
| updated_at          | TIMESTAMPTZ | NOT NULL                               |

**Índices:** `idx_mn_sprint_id`

---

### `chat_messages`
Histórico do chat em tempo real por projeto (HU10).

| Coluna     | Tipo        | Restrições                         |
|------------|-------------|------------------------------------|
| id         | UUID        | PK, NOT NULL                       |
| project_id | UUID        | FK → projects.id, NOT NULL         |
| sender_id  | UUID        | FK → users.id, NOT NULL            |
| content    | TEXT        | NOT NULL                           |
| sent_at    | TIMESTAMPTZ | NOT NULL, imutável                 |

**Índices:** `idx_cm_project_id`, `idx_cm_sent_at`

> Entrega em tempo real via WebSocket (STOMP). Esta tabela persiste o histórico para carregamento ao reabrir o chat.

---

### `story_comments`
Comentários em histórias de usuário (HU11).

| Coluna     | Tipo        | Restrições                         |
|------------|-------------|------------------------------------|
| id         | UUID        | PK, NOT NULL                       |
| story_id   | UUID        | FK → user_stories.id, NOT NULL     |
| author_id  | UUID        | FK → users.id, NOT NULL            |
| content    | TEXT        | NOT NULL                           |
| created_at | TIMESTAMPTZ | NOT NULL, imutável                 |
| updated_at | TIMESTAMPTZ | NOT NULL                           |

**Índices:** `idx_sc_story_id`

---

### `notifications`
Central de notificações do sistema por usuário (HU12).

| Coluna              | Tipo         | Restrições                                                              |
|---------------------|--------------|-------------------------------------------------------------------------|
| id                  | UUID         | PK, NOT NULL                                                            |
| user_id             | UUID         | FK → users.id, NOT NULL                                                 |
| type                | VARCHAR(50)  | NOT NULL — `TASK_ASSIGNED \| SPRINT_ENDING \| STATUS_CHANGED \| COMMENT_ADDED \| GENERAL` |
| title               | VARCHAR(255) | NOT NULL                                                                |
| message             | TEXT         | NOT NULL                                                                |
| read                | BOOLEAN      | NOT NULL, DEFAULT FALSE                                                 |
| related_entity_type | VARCHAR(100) | NULLABLE (ex: "TASK", "SPRINT", "STORY")                               |
| related_entity_id   | UUID         | NULLABLE                                                                |
| created_at          | TIMESTAMPTZ  | NOT NULL, imutável                                                      |

**Índices:** `idx_n_user_id`, `idx_n_read`

---

### `git_repositories`
Repositórios Git conectados a projetos (HU13).

| Coluna                 | Tipo         | Restrições                              |
|------------------------|--------------|------------------------------------------|
| id                     | UUID         | PK, NOT NULL                             |
| project_id             | UUID         | FK → projects.id, NOT NULL               |
| repository_url         | VARCHAR(500) | NOT NULL                                 |
| provider               | VARCHAR(50)  | NOT NULL — `GITHUB \| GITLAB \| BITBUCKET` |
| access_token_encrypted | VARCHAR(500) | NULLABLE (AES-256)                       |
| connected_by_id        | UUID         | FK → users.id, NOT NULL                  |
| created_at             | TIMESTAMPTZ  | NOT NULL, imutável                       |

**Índices:** `idx_gr_project_id`

---

### `git_commit_links`
Associação entre commits do Git e tarefas (HU13).

| Coluna        | Tipo         | Restrições                              |
|---------------|--------------|-----------------------------------------|
| id            | UUID         | PK, NOT NULL                            |
| task_id       | UUID         | FK → tasks.id, NOT NULL                 |
| repository_id | UUID         | FK → git_repositories.id, NOT NULL      |
| commit_hash   | VARCHAR(40)  | NOT NULL (SHA-1 / SHA-256)              |
| commit_message| TEXT         |                                         |
| branch_name   | VARCHAR(255) |                                         |
| committed_at  | TIMESTAMPTZ  | NOT NULL                                |
| linked_at     | TIMESTAMPTZ  | NOT NULL, imutável                      |

**Índices:** `idx_gcl_task_id`, `idx_gcl_commit_hash`

---

## Notas de Design

| # | Decisão | Justificativa |
|---|---------|---------------|
| 1 | **UUID como PK** | Elimina colisão em ambientes distribuídos, IDs não sequenciais melhoram segurança |
| 2 | **Papel global vs. por projeto** | `users.role` é apenas para `ADMIN` global; papéis funcionais (PO/SM/DEV) vivem em `project_members.role` |
| 3 | **`position_order` em user_stories** | Suporta drag-and-drop do backlog (HU02) com persistência da ordem |
| 4 | **`kanban_columns.terminal`** | Identifica dinamicamente a coluna "Concluído" para burndown — sem hardcode de status |
| 5 | **`tasks.status` + `kanban_column_id`** | `status` alimenta burndown (lógica); `kanban_column_id` é a posição visual. Mover para coluna `terminal` sincroniza `status = DONE` |
| 6 | **`task_status_history`** | Auditoria leve para rastrear progresso e alimentar futuras métricas de velocity |
| 7 | **Chat via WebSocket + tabela** | `chat_messages` persiste histórico; entrega em tempo real via STOMP/WebSocket |
| 8 | **Token Git criptografado** | `access_token_encrypted` usa AES-256 via `GitIntegrationService` — nunca armazenado em plaintext |
| 9 | **Flyway para DDL** | Migrations versionadas em `db/migration/V1__*.sql` garantem rastreabilidade e rollback |
| 10 | **`ddl-auto: validate`** | Em dev e prod o Hibernate apenas valida o schema — Flyway é responsável pela criação/alteração |

---

## Sequência de Migrações Flyway (planejada)

```
V1__create_users.sql
V2__create_projects.sql
V3__create_project_members.sql
V4__create_user_stories.sql
V5__create_sprints.sql
V6__create_sprint_stories.sql
V7__create_kanban_columns.sql
V8__create_tasks.sql
V9__create_task_status_history.sql
V10__create_meeting_notes.sql
V11__create_chat_messages.sql
V12__create_story_comments.sql
V13__create_notifications.sql
V14__create_git_repositories.sql
V15__create_git_commit_links.sql
V16__insert_default_data.sql
```
