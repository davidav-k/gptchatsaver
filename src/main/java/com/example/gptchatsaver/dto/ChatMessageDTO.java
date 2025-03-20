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
public class ChatMessageDTO {
    private Long id;
    private String sender;
    private String title;
    private String question;
    private String answer;
    private String answerHtml;
    private LocalDateTime timestamp;
}

