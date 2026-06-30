-- Mensagens do chat em tempo real.
-- project_id e sender_id são referências LÓGICAS (sem FK física) — database per service.
CREATE TABLE chat_messages (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    project_id  UUID         NOT NULL,
    sender_id   UUID         NOT NULL,
    sender_name VARCHAR(255) NOT NULL,
    content     TEXT         NOT NULL,
    sent_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_chat_messages PRIMARY KEY (id)
);

CREATE INDEX idx_cm_project_id ON chat_messages (project_id);
CREATE INDEX idx_cm_sent_at    ON chat_messages (sent_at);
