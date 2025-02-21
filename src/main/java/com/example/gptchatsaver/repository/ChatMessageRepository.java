package com.example.gptchatsaver.repository;

import com.example.gptchatsaver.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT c FROM ChatMessage c WHERE to_tsvector('russian', c.message) @@ plainto_tsquery(:query)")
    List<ChatMessage> searchMessages(String query);
}
