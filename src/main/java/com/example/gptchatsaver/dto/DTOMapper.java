package com.example.gptchatsaver.dto;

import com.example.gptchatsaver.entity.AIModel;
import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.entity.ChatSession;


public class DTOMapper {

    public static AIModelDTO toDTO(AIModel aiModel) {
        if (aiModel == null) {
            return null;
        }
        return AIModelDTO.builder()
                .id(aiModel.getId())
                .modelSlug(aiModel.getModelSlug())
                .version(aiModel.getVersion())
                .provider(aiModel.getProvider())
                .build();
    }

    public static ChatMessageDTO toDTO(ChatMessage chatMessage) {
        if (chatMessage == null) {
            return null;
        }
        return ChatMessageDTO.builder()
                .id(chatMessage.getId())
                .title(chatMessage.getTitle())
                .turnIndex(chatMessage.getTurnIndex())
                .questionId(chatMessage.getQuestionId())
                .question(chatMessage.getQuestion())
                .answerId(chatMessage.getAnswerId())
                .answer(chatMessage.getAnswer())
                .answerHtml(chatMessage.getAnswerHtml())
                .timestamp(chatMessage.getTimestamp())
                .build();
    }

    public static ChatSessionDTO toDTO(ChatSession chatSession) {
        if (chatSession == null) {
            return null;
        }

        return ChatSessionDTO.builder()
                .id(chatSession.getId())
                .sessionId(chatSession.getSessionId())
                .createdAt(chatSession.getCreatedAt())
                .aiModelVersion(chatSession.getAiModel().getVersion())
                .countMessages(chatSession.getMessages().size())
                .build();
    }
}
