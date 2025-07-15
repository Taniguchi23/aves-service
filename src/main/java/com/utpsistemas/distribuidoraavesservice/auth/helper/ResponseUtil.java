package com.utpsistemas.distribuidoraavesservice.auth.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDateTime;

public class ResponseUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void writeApiResponse(HttpServletResponse response, HttpStatus status, HttpServletRequest request, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> body = ApiResponse.builder()
                .statusCode(status.value())
                .success(false)
                .message(message)
//                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        response.getWriter().write(mapper.writeValueAsString(body));
    }
}
