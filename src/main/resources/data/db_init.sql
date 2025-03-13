-- Создаём GIN-индекс для быстрого полнотекстового поиска
CREATE INDEX IF NOT EXISTS idx_chat_message_text
    ON chat_messages
        USING GIN (to_tsvector('russian', message));
