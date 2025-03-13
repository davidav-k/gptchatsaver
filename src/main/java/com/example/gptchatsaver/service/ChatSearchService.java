package com.example.gptchatsaver.service;

import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@RequiredArgsConstructor
public class ChatSearchService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatSearchService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatMessage> searchMessages(String query, int limit) {
        return chatMessageRepository.searchMessages(query, limit);
    }
}