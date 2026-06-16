CREATE TABLE sprints (
    id             UUID         NOT NULL,
    project_id     UUID         NOT NULL,
    name           VARCHAR(255) NOT NULL,
    goal           TEXT,
    start_date     DATE         NOT NULL,
    end_date       DATE         NOT NULL,
    status         VARCHAR(30)  NOT NULL DEFAULT 'PLANNED',
    created_by_id  UUID         NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL,
    updated_at     TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_sprints       PRIMARY KEY (id),
    CONSTRAINT fk_spr_project   FOREIGN KEY (project_id)    REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_spr_creator   FOREIGN KEY (created_by_id) REFERENCES users    (id),
    CONSTRAINT chk_spr_status   CHECK (status     IN ('PLANNED','IN_PROGRESS','COMPLETED')),
    CONSTRAINT chk_spr_dates    CHECK (end_date    >  start_date)
);

CREATE INDEX idx_sprints_project_id ON sprints (project_id);
CREATE INDEX idx_sprints_status     ON sprints (status);
