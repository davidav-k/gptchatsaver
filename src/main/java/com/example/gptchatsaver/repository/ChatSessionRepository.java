package com.example.gptchatsaver.repository;

import com.example.gptchatsaver.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    ChatSession findBySessionId(String sessionId);

}
