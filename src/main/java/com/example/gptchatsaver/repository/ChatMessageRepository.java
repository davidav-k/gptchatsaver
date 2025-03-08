package com.example.gptchatsaver.repository;

import com.example.gptchatsaver.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query(value = "SELECT * FROM chat_messages WHERE to_tsvector('russian', message) @@ plainto_tsquery(:query)",
            nativeQuery = true)
    List<ChatMessage> searchMessages(@Param("query") String query);
}
