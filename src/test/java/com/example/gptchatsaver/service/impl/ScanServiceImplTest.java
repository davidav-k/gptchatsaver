package com.example.gptchatsaver.service.impl;

import com.example.gptchatsaver.entity.AIModel;
import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.entity.ChatSession;
import com.example.gptchatsaver.exception.ChatScanException;
import com.example.gptchatsaver.exception.ScanningException;
import com.example.gptchatsaver.repository.AiModelRepository;
import com.example.gptchatsaver.repository.ChatMessageRepository;
import com.example.gptchatsaver.repository.ChatSessionRepository;
import com.example.gptchatsaver.service.SeleniumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension .class)
class ScanServiceImplTest {

    @Mock
    private SeleniumService seleniumService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private ChatSessionRepository chatSessionRepository;

    @Mock
    private AiModelRepository aiModelRepository;

    @Mock
    private WebDriver webDriver;

    @InjectMocks
    private ScanServiceImpl scanService;

    private AIModel aiModel;
    private ChatSession chatSession;
    private List<ChatMessage> messages;

    @BeforeEach
    void setUp() {
        aiModel = new AIModel();
        aiModel.setModelSlug("gpt-4");

        chatSession = new ChatSession();
        chatSession.setSessionId("test-session-id");
        chatSession.setAiModel(aiModel);

        messages = new ArrayList<>();
        ChatMessage message = new ChatMessage();
        message.setTitle("Test Title");
        message.setQuestion("Test Question");
        message.setAnswer("Test Answer");
        message.setChatSession(chatSession);
        messages.add(message);
    }

    @Test
    void scanChatSuccessfully() {
        when(seleniumService.initDriver()).thenReturn(webDriver);
        when(seleniumService.extractAIModelInfo(webDriver)).thenReturn(aiModel);
        when(aiModelRepository.save(aiModel)).thenReturn(aiModel);
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(chatSession);
        when(seleniumService.extractChatMessages(any(ChatSession.class), eq(webDriver))).thenReturn(messages);

        scanService.scanChat();

        verify(seleniumService).initDriver();
        verify(seleniumService).extractAIModelInfo(webDriver);
        verify(aiModelRepository).save(aiModel);
        verify(chatSessionRepository).save(any(ChatSession.class));
        verify(seleniumService).extractChatMessages(any(ChatSession.class), eq(webDriver));
        verify(chatMessageRepository).existsByTitleAndQuestion("Test Title", "Test Question");
        verify(seleniumService).closeDriver(webDriver);
    }

    @Test
    void scanChatSavesOnlyNewMessages() {
        when(seleniumService.initDriver()).thenReturn(webDriver);
        when(seleniumService.extractAIModelInfo(webDriver)).thenReturn(aiModel);
        when(aiModelRepository.save(aiModel)).thenReturn(aiModel);
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(chatSession);
        when(seleniumService.extractChatMessages(any(ChatSession.class), eq(webDriver))).thenReturn(messages);
        when(chatMessageRepository.existsByTitleAndQuestion("Test Title", "Test Question")).thenReturn(false);

        scanService.scanChat();

        verify(chatMessageRepository).saveAll(anyList());
    }

    @Test
    void scanChatDoesNotSaveExistingMessages() {
        when(seleniumService.initDriver()).thenReturn(webDriver);
        when(seleniumService.extractAIModelInfo(webDriver)).thenReturn(aiModel);
        when(aiModelRepository.save(aiModel)).thenReturn(aiModel);
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(chatSession);
        when(seleniumService.extractChatMessages(any(ChatSession.class), eq(webDriver))).thenReturn(messages);
        when(chatMessageRepository.existsByTitleAndQuestion("Test Title", "Test Question")).thenReturn(true);

        scanService.scanChat();

        verify(chatMessageRepository, never()).saveAll(anyList());
    }

    @Test
    void scanChatHandlesEmptyMessageList() throws Exception {
        when(seleniumService.initDriver()).thenReturn(webDriver);
        when(seleniumService.extractAIModelInfo(webDriver)).thenReturn(aiModel);
        when(aiModelRepository.save(aiModel)).thenReturn(aiModel);
        when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(chatSession);
        when(seleniumService.extractChatMessages(any(ChatSession.class), eq(webDriver))).thenReturn(new ArrayList<>());

        scanService.scanChat();

        verify(chatMessageRepository, never()).saveAll(anyList());
    }

    @Test
    void scanChatThrowsExceptionWhenScanningFails() {
        when(seleniumService.initDriver()).thenReturn(webDriver);
        when(seleniumService.extractAIModelInfo(webDriver)).thenThrow(new ScanningException("Failed to extract AI model"));

        assertThrows(ChatScanException.class, () -> scanService.scanChat());
        verify(seleniumService).closeDriver(webDriver);
    }

    @Test
    void scanChatHandlesUnexpectedExceptions() {
        when(seleniumService.initDriver()).thenReturn(webDriver);
        when(seleniumService.extractAIModelInfo(webDriver)).thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(ChatScanException.class, () -> scanService.scanChat());
        verify(seleniumService).closeDriver(webDriver);
    }

}