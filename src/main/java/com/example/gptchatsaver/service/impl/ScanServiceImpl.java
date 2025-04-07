package com.example.gptchatsaver.service.impl;

import com.example.gptchatsaver.entity.AIModel;
import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.entity.ChatSession;
import com.example.gptchatsaver.exception.ChatScanException;
import com.example.gptchatsaver.exception.ScanningException;
import com.example.gptchatsaver.repository.AiModelRepository;
import com.example.gptchatsaver.repository.ChatMessageRepository;
import com.example.gptchatsaver.repository.ChatSessionRepository;
import com.example.gptchatsaver.service.ScanService;
import com.example.gptchatsaver.service.SeleniumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanServiceImpl implements ScanService {

    private final SeleniumService seleniumService;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AiModelRepository aiModelRepository;


    @Override
    @Transactional
    public void scanChat() {

        WebDriver driver = seleniumService.initDriver();

        try {
            AIModel aiModel = seleniumService.extractAIModelInfo(driver);
            AIModel savedAIModel = aiModelRepository.save(aiModel);
            log.debug("AIModel saved with id: {}", savedAIModel.getModelSlug());

            ChatSession chatSession = createChatSession(savedAIModel);
            List<ChatMessage> messages = seleniumService.extractChatMessages(chatSession, driver);

            saveChatMessages(messages);

            log.info("Chat scanning completed successfully");
        } catch (ScanningException ex) {
            log.error("Error during web scanning: {}", ex.getMessage(), ex);
            throw new ChatScanException("Failed to scanning chat data", ex);
        } catch (Exception ex) {
            log.error("Unexpected error during chat scanning: {}", ex.getMessage(), ex);
            throw new ChatScanException("Unexpected error during chat scanning", ex);
        } finally {
            seleniumService.closeDriver(driver);
        }
    }

    private ChatSession createChatSession(AIModel aiModel) {
        ChatSession chatSession = ChatSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .aiModel(aiModel)
                .createdAt(LocalDateTime.now())
                .build();
        log.debug("Creating new chat session with id: {}", chatSession.getSessionId());
        return chatSessionRepository.save(chatSession);
    }

    private void saveChatMessages(List<ChatMessage> messages) {
        if (messages.isEmpty()) {
            log.info("No new messages to save");
            return;
        }

        List<ChatMessage> newMessages = messages.stream()
                .filter(message -> !chatMessageRepository.existsByTitleAndQuestion(
                        message.getTitle(), message.getQuestion()))
                .toList();

        if (newMessages.isEmpty()) {
            log.info("All messages already exist in the database");
            return;
        }

        log.info("Saving {} new chat messages", newMessages.size());
        chatMessageRepository.saveAll(newMessages);
    }

}
