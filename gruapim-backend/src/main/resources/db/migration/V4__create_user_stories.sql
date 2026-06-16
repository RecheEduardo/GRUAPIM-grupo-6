CREATE TABLE user_stories (
    id             UUID         NOT NULL,
    project_id     UUID         NOT NULL,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    priority       VARCHAR(20)  NOT NULL DEFAULT 'MEDIUM',
    status         VARCHAR(30)  NOT NULL DEFAULT 'BACKLOG',
    story_points   INTEGER,
    position_order INTEGER      NOT NULL DEFAULT 0,
    created_by_id  UUID         NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL,
    updated_at     TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_user_stories  PRIMARY KEY (id),
    CONSTRAINT fk_us_project    FOREIGN KEY (project_id)    REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_us_creator    FOREIGN KEY (created_by_id) REFERENCES users    (id),
    CONSTRAINT chk_us_priority  CHECK (priority IN ('HIGH','MEDIUM','LOW')),
    CONSTRAINT chk_us_status    CHECK (status   IN ('BACKLOG','IN_SPRINT','IN_PROGRESS','DONE')),
    CONSTRAINT chk_us_sp        CHECK (story_points IS NULL OR story_points > 0)
);

CREATE INDEX idx_us_project_id ON user_stories (project_id);
CREATE INDEX idx_us_status     ON user_stories (status);
CREATE INDEX idx_us_priority   ON user_stories (priority);
