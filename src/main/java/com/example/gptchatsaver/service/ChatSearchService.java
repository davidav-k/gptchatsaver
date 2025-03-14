package com.example.gptchatsaver.service;

import com.example.gptchatsaver.entity.ChatMessage;

import java.util.List;

public interface ChatSearchService {

    List<ChatMessage> searchMessages(String query, int limit);

}
