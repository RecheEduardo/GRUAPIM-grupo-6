-- Strangler Fig: o contexto de Colaboração e Comunicação foi extraído para o
-- microsserviço gruapim-collaboration-service (banco próprio: gruapim_collab).
-- O monólito deixa de ser dono dessas tabelas e descontinua sua escrita.
--
-- Observação: o microsserviço recria o schema equivalente via suas próprias
-- migrations Flyway (V1..V3). Caso haja dados históricos a preservar, executar
-- antes um script de data migration para o banco gruapim_collab.

DROP TABLE IF EXISTS chat_messages;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS meeting_notes;
