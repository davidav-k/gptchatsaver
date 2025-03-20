CREATE TABLE IF NOT EXISTS ai_models (
                                         id SERIAL PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
                                         version VARCHAR(255),
                                         provider VARCHAR(255)
    );
CREATE TABLE IF NOT EXISTS chat_sessions (
                                             id SERIAL PRIMARY KEY,
                                             session_id VARCHAR(255) UNIQUE NOT NULL,
                                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                             ai_model_id INT NOT NULL REFERENCES ai_models(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS chat_messages (
                                             id SERIAL PRIMARY KEY,
                                             chat_session_id INT NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
                                             sender VARCHAR(255),
                                             title VARCHAR(255),
                                             question TEXT NOT NULL,
                                             answer TEXT NOT NULL,
                                             answer_html TEXT NOT NULL,
                                             timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
DO $$
    BEGIN
        IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'chat_messages') THEN
            CREATE INDEX IF NOT EXISTS idx_chat_message_text
                ON chat_messages
                    USING GIN (to_tsvector('russian', answer));
        END IF;
    END $$;
