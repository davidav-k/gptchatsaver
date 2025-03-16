package com.example.gptchatsaver.resource;

import com.example.gptchatsaver.service.ChatScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "${api.endpoint.base-url}/chat")
@RequiredArgsConstructor
public class ChatScanResource {

    private final ChatScanService chatScanService;

    @PostMapping("/scan")
    public ResponseEntity<String> scanChat() {
        try {
            chatScanService.scanChat();
            return ResponseEntity.ok("Сканирование чата завершено успешно");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка при сканировании чата: " + ex.getMessage());
        }
    }
}

