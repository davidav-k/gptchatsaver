package com.example.gptchatsaver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "ai_model_id", nullable = false)
    private AIModel aiModel;

    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL)
    private List<ChatMessage> messages;
}
