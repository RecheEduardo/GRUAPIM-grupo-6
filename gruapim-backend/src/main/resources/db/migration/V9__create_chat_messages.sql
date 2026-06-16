CREATE TABLE chat_messages (
    id          UUID        NOT NULL,
    project_id  UUID        NOT NULL,
    sender_id   UUID        NOT NULL,
    content     TEXT        NOT NULL,
    sent_at     TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_chat_messages PRIMARY KEY (id),
    CONSTRAINT fk_cm_project    FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_cm_sender     FOREIGN KEY (sender_id)  REFERENCES users    (id)
);

CREATE TABLE story_comments (
    id          UUID        NOT NULL,
    story_id    UUID        NOT NULL,
    author_id   UUID        NOT NULL,
    content     TEXT        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_story_comments    PRIMARY KEY (id),
    CONSTRAINT fk_sc_story          FOREIGN KEY (story_id)  REFERENCES user_stories (id) ON DELETE CASCADE,
    CONSTRAINT fk_sc_author         FOREIGN KEY (author_id) REFERENCES users        (id)
);

CREATE INDEX idx_cm_project_id ON chat_messages (project_id);
CREATE INDEX idx_sc_story_id   ON story_comments (story_id);
