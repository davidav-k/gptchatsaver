package com.example.gptchatsaver.service.impl;

import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.repository.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    private List<ChatMessage> mockMessages;
    private Page<ChatMessage> mockPage;

    @BeforeEach
    void setUp() {
        ChatMessage message1 = new ChatMessage();
        ChatMessage message2 = new ChatMessage();
        mockMessages = Arrays.asList(message1, message2);
        mockPage = new PageImpl<>(mockMessages);
    }

    @Test
    void searchQuestionReturnsMatchingMessages() {
        when(chatMessageRepository.searchQuestionPaginated(anyString(), any(Pageable.class))).thenReturn(mockPage);

        List<ChatMessage> result = searchService.searchQuestion("test query", 10);

        assertEquals(mockMessages, result);
        verify(chatMessageRepository).searchQuestionPaginated("test query", Pageable.ofSize(10));
    }


    @Test
    void searchQuestionWithEmptyQueryReturnsResults() {
        when(chatMessageRepository.searchQuestionPaginated(eq(""), any(Pageable.class))).thenReturn(mockPage);

        List<ChatMessage> result = searchService.searchQuestion("", 10);

        assertEquals(mockMessages, result);
    }

    @Test
    void searchQuestionWithZeroOrNegativeLimitHandledGracefully() {
        List<ChatMessage> result = searchService.searchQuestion("test", 0);

        assertTrue(result.isEmpty());
    }

    @Test
    void searchAnswerReturnsMatchingMessages() {
        when(chatMessageRepository.searchAnswerPaginated(anyString(), any(Pageable.class))).thenReturn(mockPage);

        List<ChatMessage> result = searchService.searchAnswer("test query", 5);

        assertEquals(mockMessages, result);
        verify(chatMessageRepository).searchAnswerPaginated("test query", Pageable.ofSize(5));
    }

    @Test
    void searchAnswerWithEmptyQueryReturnsResults() {
        when(chatMessageRepository.searchAnswerPaginated(eq(""), any(Pageable.class))).thenReturn(mockPage);

        List<ChatMessage> result = searchService.searchAnswer("", 10);

        assertEquals(mockMessages, result);
    }

    @Test
    void searchAnswerWithZeroOrNegativeLimitHandledGracefully() {
        List<ChatMessage> result = searchService.searchAnswer("test", 0);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllReturnsFirstTenMessages() {
        when(chatMessageRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        List<ChatMessage> result = searchService.getAll();

        assertEquals(mockMessages, result);
        verify(chatMessageRepository).findAll(Pageable.ofSize(10));
    }

    @Test
    void getAllReturnsEmptyListWhenNoMessages() {
        when(chatMessageRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        List<ChatMessage> result = searchService.getAll();

        assertTrue(result.isEmpty());
    }

}