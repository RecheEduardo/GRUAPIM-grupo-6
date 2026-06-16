CREATE TABLE project_members (
    id          UUID        NOT NULL,
    project_id  UUID        NOT NULL,
    user_id     UUID        NOT NULL,
    role        VARCHAR(50) NOT NULL,
    joined_at   TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_project_members PRIMARY KEY (id),
    CONSTRAINT uq_project_user   UNIQUE (project_id, user_id),
    CONSTRAINT fk_pm_project     FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_pm_user        FOREIGN KEY (user_id)    REFERENCES users    (id),
    CONSTRAINT chk_pm_role       CHECK (role IN ('PRODUCT_OWNER','SCRUM_MASTER','DEVELOPER'))
);

CREATE INDEX idx_pm_project_id ON project_members (project_id);
CREATE INDEX idx_pm_user_id    ON project_members (user_id);
