package com.example.gptchatsaver.service.impl;

import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.repository.ChatMessageRepository;
import com.example.gptchatsaver.service.ChatSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatSearchServiceImpl implements ChatSearchService {
    private final ChatMessageRepository chatMessageRepository;

//    public ChatSearchServiceImpl(ChatMessageRepository chatMessageRepository) {
//        this.chatMessageRepository = chatMessageRepository;
//    }

    @Override
    public List<ChatMessage> searchMessages(String query, int limit) {
        return chatMessageRepository.searchMessages(query, limit);
    }
}