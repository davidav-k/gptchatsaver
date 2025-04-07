package com.example.gptchatsaver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "chat_session_id", nullable = false)
    private ChatSession chatSession;
    @ManyToOne
    @JoinColumn(name = "ai_model_id", nullable = false)
    private AIModel aiModel;
    private String title;
    private Integer turnIndex;
    private String questionId;
    private String answerId;
    @Column(columnDefinition = "TEXT")
    private String question;
    @Column(columnDefinition = "TEXT")
    private String answer;
    @Column(columnDefinition = "TEXT")
    private String answerHtml;

    private LocalDateTime timestamp;

}
