package com.example.gptchatsaver.resource;


import com.example.gptchatsaver.entity.ChatMessage;
import com.example.gptchatsaver.service.impl.ChatSearchServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "${api.endpoint.base-url}/chat")
@RequiredArgsConstructor
public class MessageResource {


    private final ChatSearchServiceImpl chatSearchService;

//    public MessageResource(ChatSearchServiceImpl chatSearchService) {
//        this.chatSearchService = chatSearchService;
//    }

    @PostMapping("/search")
    public List<ChatMessage> searchMessages(@RequestParam String query,
                                            @RequestParam(defaultValue = "10") int limit) {
        return chatSearchService.searchMessages(query, limit);
    }
}