package com.example.gptchatsaver.service.impl;

import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.repository.ChatMessageRepository;
import com.example.gptchatsaver.service.ChatSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatSearchServiceImpl implements ChatSearchService {
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    @Override
    public List<ChatMessage> searchQuestion(String query, int limit) {
        return chatMessageRepository.searchQuestion(query, limit);
    }

    @Override
    public List<ChatMessage> searchAnswer(String query, int limit) {
        return chatMessageRepository.searchAnswer(query, limit);
    }

}