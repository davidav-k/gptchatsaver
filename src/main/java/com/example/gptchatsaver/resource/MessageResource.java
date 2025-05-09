package com.example.gptchatsaver.resource;


import com.example.gptchatsaver.domen.Response;
import com.example.gptchatsaver.dto.ChatMessageDTO;
import com.example.gptchatsaver.dto.DTOMapper;
import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.service.SearchService;
import com.example.gptchatsaver.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "${api.endpoint.base-url}/chat")
@RequiredArgsConstructor
public class MessageResource {


    private final SearchService searchService;


    @PostMapping("/search/question")
    public ResponseEntity<Response> searchQuestion(@RequestParam String query,
                                                   @RequestParam(defaultValue = "10") int limit,
                                                   HttpServletRequest request) {
        log.info("Start search question: {}", query);
        List<ChatMessage> messages = searchService.searchQuestion(query, limit);
        List<ChatMessageDTO> messageDTOs = messages.stream()
                .map(DTOMapper::toDTO)
                .toList();

        return ResponseEntity.ok().body(RequestUtils.getResponse(
                request,
                Map.of("messages", messageDTOs),
                "Found successful into database.",
                HttpStatus.OK));

    }

    @PostMapping("/search/answer")
    public ResponseEntity<Response> searchAnswer(@RequestParam String query,
                                                 @RequestParam(defaultValue = "10") int limit,
                                                 HttpServletRequest request) {
        log.info("Start search answer: {}", query);
        List<ChatMessage> messages = searchService.searchAnswer(query, limit);
        List<ChatMessageDTO> messageDTOs = messages.stream()
                .map(DTOMapper::toDTO)
                .toList();

        return ResponseEntity.ok().body(RequestUtils.getResponse(
                request,
                Map.of("messages", messageDTOs),
                "Found successful into database.",
                HttpStatus.OK));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAll(HttpServletRequest request) {
        log.info("Start get all messages");
        List<ChatMessage> messages = searchService.getAll();
        List<ChatMessageDTO> messageDTOs = messages.stream()
                .map(DTOMapper::toDTO)
                .toList();

        return ResponseEntity.ok().body(RequestUtils.getResponse(
                request,
                Map.of("messages", messageDTOs),
                "Found successful into database.",
                HttpStatus.OK));
    }

}