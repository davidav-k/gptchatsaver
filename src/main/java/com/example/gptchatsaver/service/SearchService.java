package com.example.gptchatsaver.service;

import com.example.gptchatsaver.entity.ChatMessage;

import java.util.List;

public interface SearchService {

    List<ChatMessage> searchQuestion(String query, int limit);

    List<ChatMessage> searchAnswer(String query, int limit);

    List<ChatMessage> getAll();
}
