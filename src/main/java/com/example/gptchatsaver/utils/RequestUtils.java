package com.example.gptchatsaver.utils;


import com.example.gptchatsaver.domen.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


public class RequestUtils {


    public static Response getResponse(HttpServletRequest request,
                                       Map<?, ?> data,
                                       String message,
                                       HttpStatus status) {
        return new Response(
                LocalDateTime.now().toString(),
                status.value(),
                request.getRequestURI(),
                HttpStatus.valueOf(status.value()),
                message,
                EMPTY,
                data);
    }

    public static Response getErrorResponse(HttpServletRequest request,
                                             HttpServletResponse response,
                                             Exception exception,
                                             HttpStatus status) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        return new Response(
                LocalDateTime.now().toString(),
                status.value(),
                request.getRequestURI(),
                HttpStatus.valueOf(status.value()),
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                getRootCauseMessage(exception),
                Map.of());
    }




}

