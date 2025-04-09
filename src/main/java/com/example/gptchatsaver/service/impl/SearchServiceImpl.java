package com.example.gptchatsaver.service.impl;

import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.repository.ChatMessageRepository;
import com.example.gptchatsaver.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    @Override
    public List<ChatMessage> searchQuestion(String query, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }

        Pageable pageable = Pageable.ofSize(limit);
        return chatMessageRepository.searchQuestionPaginated(query, pageable).getContent();
    }

    @Override
    public List<ChatMessage> searchAnswer(String query, int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        Pageable pageable = Pageable.ofSize(limit);

        return chatMessageRepository.searchAnswerPaginated(query, pageable).getContent();

    }

    @Override
    public List<ChatMessage> getAll() {
        Pageable pageable = Pageable.ofSize(10);

        return chatMessageRepository.findAll(pageable).getContent();
    }

}