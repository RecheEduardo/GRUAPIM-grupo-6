CREATE TABLE projects (
    id             UUID         NOT NULL,
    name           VARCHAR(255) NOT NULL,
    description    TEXT,
    created_by_id  UUID         NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL,
    updated_at     TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_projects      PRIMARY KEY (id),
    CONSTRAINT fk_proj_creator  FOREIGN KEY (created_by_id) REFERENCES users (id)
);

CREATE INDEX idx_projects_created_by ON projects (created_by_id);
