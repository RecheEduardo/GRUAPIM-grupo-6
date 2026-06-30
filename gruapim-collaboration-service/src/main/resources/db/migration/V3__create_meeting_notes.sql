-- Atas de reunião (Review / Retrospectiva).
-- sprint_id, project_id e created_by_id são referências LÓGICAS — database per service.
-- project_id desnormalizado para suportar consulta "atas do projeto" sem join entre serviços.
CREATE TABLE meeting_notes (
    id                  UUID         NOT NULL DEFAULT gen_random_uuid(),
    sprint_id           UUID         NOT NULL,
    project_id          UUID         NOT NULL,
    type                VARCHAR(30)  NOT NULL,
    discussed_points    TEXT,
    decisions           TEXT,
    improvement_actions TEXT,
    created_by_id       UUID         NOT NULL,
    created_by_name     VARCHAR(255) NOT NULL,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_meeting_notes PRIMARY KEY (id)
);

CREATE INDEX idx_mn_sprint_id  ON meeting_notes (sprint_id);
CREATE INDEX idx_mn_project_id ON meeting_notes (project_id);
