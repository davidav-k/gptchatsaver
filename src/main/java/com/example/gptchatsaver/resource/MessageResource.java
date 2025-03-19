package com.example.gptchatsaver.resource;


import com.example.gptchatsaver.dto.ChatMessageDTO;
import com.example.gptchatsaver.dto.DTOMapper;
import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.service.impl.ChatSearchServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "${api.endpoint.base-url}/chat")
@RequiredArgsConstructor
public class MessageResource {


    private final ChatSearchServiceImpl chatSearchService;


    @PostMapping("/search")
    public List<ChatMessageDTO> searchMessages(@RequestParam String query,
                                               @RequestParam(defaultValue = "10") int limit) {
        List<ChatMessage> messages = chatSearchService.searchMessages(query, limit);
        return messages.stream()
                .map(DTOMapper::toDTO)
                .collect(Collectors.toList());
    }


}