package com.example.gptchatsaver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionDTO {
    private Long id;
    private String sessionId;
    private LocalDateTime createdAt;
    private String aiModelVersion;
    private int countMessages;
}
