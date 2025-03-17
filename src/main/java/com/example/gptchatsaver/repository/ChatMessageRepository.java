package com.example.gptchatsaver.repository;

import com.example.gptchatsaver.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query(value = """
        SELECT *, ts_rank(to_tsvector('russian', question), plainto_tsquery('russian', :query)) AS rank
        FROM chat_messages
        WHERE to_tsvector('russian', question) @@ plainto_tsquery('russian', :query)
        ORDER BY rank DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<ChatMessage> searchMessages(@Param("query") String query, @Param("limit") int limit);
}
