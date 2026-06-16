CREATE TABLE notifications (
    id                   UUID        NOT NULL,
    user_id              UUID        NOT NULL,
    type                 VARCHAR(50) NOT NULL,
    title                VARCHAR(255) NOT NULL,
    message              TEXT        NOT NULL,
    read                 BOOLEAN     NOT NULL DEFAULT FALSE,
    related_entity_type  VARCHAR(100),
    related_entity_id    UUID,
    created_at           TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT fk_n_user        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_n_type       CHECK (type IN ('TASK_ASSIGNED','SPRINT_STARTED','SPRINT_ENDED','STORY_UPDATED','COMMENT_ADDED','MEMBER_ADDED'))
);

CREATE INDEX idx_n_user_id ON notifications (user_id);
CREATE INDEX idx_n_read    ON notifications (read);
