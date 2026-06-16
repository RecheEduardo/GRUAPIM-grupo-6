CREATE TABLE kanban_columns (
    id          UUID         NOT NULL,
    project_id  UUID         NOT NULL,
    name        VARCHAR(100) NOT NULL,
    position    INTEGER      NOT NULL,
    terminal    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_kanban_columns    PRIMARY KEY (id),
    CONSTRAINT fk_kc_project        FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE
);

CREATE INDEX idx_kc_project_id ON kanban_columns (project_id);
