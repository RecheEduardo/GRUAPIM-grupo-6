CREATE TABLE git_repositories (
    id                      UUID         NOT NULL,
    project_id              UUID         NOT NULL,
    repository_url          VARCHAR(500) NOT NULL,
    provider                VARCHAR(50)  NOT NULL,
    access_token_encrypted  VARCHAR(500),
    connected_by_id         UUID         NOT NULL,
    created_at              TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_git_repositories  PRIMARY KEY (id),
    CONSTRAINT fk_gr_project        FOREIGN KEY (project_id)      REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_gr_connected_by   FOREIGN KEY (connected_by_id) REFERENCES users    (id),
    CONSTRAINT uq_gr_project_url    UNIQUE (project_id, repository_url)
);

CREATE TABLE git_commit_links (
    id              UUID        NOT NULL,
    task_id         UUID        NOT NULL,
    repository_id   UUID        NOT NULL,
    commit_hash     VARCHAR(40) NOT NULL,
    commit_message  TEXT,
    branch_name     VARCHAR(255),
    committed_at    TIMESTAMPTZ NOT NULL,
    linked_at       TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_git_commit_links  PRIMARY KEY (id),
    CONSTRAINT fk_gcl_task          FOREIGN KEY (task_id)       REFERENCES tasks            (id) ON DELETE CASCADE,
    CONSTRAINT fk_gcl_repository    FOREIGN KEY (repository_id) REFERENCES git_repositories (id) ON DELETE CASCADE,
    CONSTRAINT uq_gcl_task_hash     UNIQUE (task_id, commit_hash)
);

CREATE INDEX idx_gr_project_id  ON git_repositories (project_id);
CREATE INDEX idx_gcl_task_id    ON git_commit_links (task_id);
CREATE INDEX idx_gcl_commit_hash ON git_commit_links (commit_hash);
