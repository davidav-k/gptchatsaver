package com.example.gptchatsaver.resource;

import com.example.gptchatsaver.domen.Response;
import com.example.gptchatsaver.service.ScanService;
import com.example.gptchatsaver.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static java.util.Collections.emptyMap;
@Slf4j
@RestController
@RequestMapping(path = "${api.endpoint.base-url}/chat")
@RequiredArgsConstructor
public class ChatScanResource {

    private final ScanService scanService;

    @PostMapping("/scan")
    public ResponseEntity<Response> scanChat(HttpServletRequest request, HttpServletResponse response) {
        log.info("Start scan chat");
        try {
            scanService.scanChat();

            return ResponseEntity.created(URI.create("")).body(RequestUtils.getResponse(
                    request,
                    emptyMap(),
                    "Chat scan completed successfully",
                    HttpStatus.CREATED));
        } catch (Exception ex) {
            return ResponseEntity.ok().body(RequestUtils.getErrorResponse(
                    request,
                    response,
                    ex,
                    HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}

