CREATE TABLE meeting_notes (
    id                  UUID        NOT NULL,
    sprint_id           UUID        NOT NULL,
    type                VARCHAR(30) NOT NULL,
    discussed_points    TEXT,
    decisions           TEXT,
    improvement_actions TEXT,
    created_by_id       UUID        NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_meeting_notes     PRIMARY KEY (id),
    CONSTRAINT fk_mn_sprint         FOREIGN KEY (sprint_id)     REFERENCES sprints (id) ON DELETE CASCADE,
    CONSTRAINT fk_mn_creator        FOREIGN KEY (created_by_id) REFERENCES users   (id),
    CONSTRAINT chk_mn_type          CHECK (type IN ('DAILY','PLANNING','REVIEW','RETROSPECTIVE'))
);

CREATE INDEX idx_mn_sprint_id ON meeting_notes (sprint_id);
