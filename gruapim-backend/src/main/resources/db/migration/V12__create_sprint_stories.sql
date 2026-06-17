CREATE TABLE sprint_stories (
    id         UUID        NOT NULL,
    sprint_id  UUID        NOT NULL,
    story_id   UUID        NOT NULL,
    added_at   TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_sprint_stories    PRIMARY KEY (id),
    CONSTRAINT uq_sprint_story      UNIQUE (sprint_id, story_id),
    CONSTRAINT fk_ss_sprint         FOREIGN KEY (sprint_id) REFERENCES sprints     (id) ON DELETE CASCADE,
    CONSTRAINT fk_ss_story          FOREIGN KEY (story_id)  REFERENCES user_stories (id) ON DELETE CASCADE
);

CREATE INDEX idx_ss_sprint_id ON sprint_stories (sprint_id);
CREATE INDEX idx_ss_story_id  ON sprint_stories (story_id);
