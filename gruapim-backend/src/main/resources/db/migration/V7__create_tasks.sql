CREATE TABLE tasks (
    id               UUID         NOT NULL,
    story_id         UUID         NOT NULL,
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    assignee_id      UUID,
    status           VARCHAR(30)  NOT NULL DEFAULT 'TODO',
    kanban_column_id UUID,
    created_by_id    UUID         NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL,
    updated_at       TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_tasks             PRIMARY KEY (id),
    CONSTRAINT fk_task_story        FOREIGN KEY (story_id)         REFERENCES user_stories   (id) ON DELETE CASCADE,
    CONSTRAINT fk_task_assignee     FOREIGN KEY (assignee_id)      REFERENCES users          (id),
    CONSTRAINT fk_task_kanban_col   FOREIGN KEY (kanban_column_id) REFERENCES kanban_columns (id),
    CONSTRAINT fk_task_creator      FOREIGN KEY (created_by_id)    REFERENCES users          (id),
    CONSTRAINT chk_task_status      CHECK (status IN ('TODO','IN_PROGRESS','REVIEW','DONE'))
);

CREATE TABLE task_status_history (
    id          UUID        NOT NULL,
    task_id     UUID        NOT NULL,
    old_status  VARCHAR(30),
    new_status  VARCHAR(30) NOT NULL,
    changed_by  UUID        NOT NULL,
    changed_at  TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_task_status_history   PRIMARY KEY (id),
    CONSTRAINT fk_tsh_task              FOREIGN KEY (task_id)    REFERENCES tasks (id) ON DELETE CASCADE,
    CONSTRAINT fk_tsh_changed_by        FOREIGN KEY (changed_by) REFERENCES users (id)
);

CREATE INDEX idx_tasks_story_id    ON tasks (story_id);
CREATE INDEX idx_tasks_assignee_id ON tasks (assignee_id);
CREATE INDEX idx_tasks_status      ON tasks (status);
