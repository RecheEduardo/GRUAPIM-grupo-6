-- Central de notificações.
-- user_id é referência LÓGICA (sem FK física) — database per service.
CREATE TABLE notifications (
    id                  UUID         NOT NULL DEFAULT gen_random_uuid(),
    user_id             UUID         NOT NULL,
    type                VARCHAR(50)  NOT NULL,
    title               VARCHAR(255) NOT NULL,
    message             TEXT         NOT NULL,
    read                BOOLEAN      NOT NULL DEFAULT FALSE,
    related_entity_type VARCHAR(100),
    related_entity_id   UUID,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE INDEX idx_n_user_id ON notifications (user_id);
CREATE INDEX idx_n_read    ON notifications (read);
