package com.example.gptchatsaver.repository;

import com.example.gptchatsaver.entity.ChatMessage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query(value = """
        SELECT m.*, ts_rank(to_tsvector('russian', m.question), plainto_tsquery('russian', :query)) AS rank
        FROM chat_messages m
        WHERE to_tsvector('russian', m.question) @@ plainto_tsquery('russian', :query)
        ORDER BY rank DESC
    """,
            countQuery = """
        SELECT COUNT(*)
        FROM chat_messages m
        WHERE to_tsvector('russian', m.question) @@ plainto_tsquery('russian', :query)
    """,
            nativeQuery = true)
    Page<ChatMessage> searchQuestionPaginated(@Param("query") String query, Pageable pageable);


    @Query(value = """
        SELECT m.*, ts_rank(to_tsvector('russian', m.answer), plainto_tsquery('russian', :query)) AS rank
        FROM chat_messages m
        WHERE to_tsvector('russian', m.answer) @@ plainto_tsquery('russian', :query)
        ORDER BY rank DESC
    """,
            countQuery = """
        SELECT COUNT(*)\s
        FROM chat_messages m
        WHERE to_tsvector('russian', m.answer) @@ plainto_tsquery('russian', :query)
   \s""",
            nativeQuery = true)
    Page<ChatMessage> searchAnswerPaginated(@Param("query") String query, Pageable pageable);

    boolean existsByTitleAndQuestion(String title, String question);

    List<ChatMessage> findByChatSessionIdOrderByTurnIndexAsc(Long chatSessionId);

}
