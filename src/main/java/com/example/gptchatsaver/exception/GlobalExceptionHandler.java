package com.example.gptchatsaver.exception;

import com.example.gptchatsaver.domen.Response;
import com.example.gptchatsaver.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ScanningException.class)
    public ResponseEntity<Response> handleScrapingException(ScanningException ex, HttpServletRequest request) {
        log.error("Scraping error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RequestUtils.getResponse(
                        request,
                        Map.of("error", "scraping_error"),
                        "Error occurred during web scraping: " + ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(ChatScanException.class)
    public ResponseEntity<Response> handleChatScanException(ChatScanException ex, HttpServletRequest request) {
        log.error("Chat scan error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RequestUtils.getResponse(
                        request,
                        Map.of("error", "chat_scan_error"),
                        "Error occurred during chat scan: " + ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RequestUtils.getResponse(
                        request,
                        Map.of("error", "internal_server_error"),
                        "An unexpected error occurred",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }
}