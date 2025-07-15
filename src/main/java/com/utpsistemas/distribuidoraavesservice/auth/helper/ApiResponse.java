package com.utpsistemas.distribuidoraavesservice.auth.helper;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApiResponse<T> {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private boolean success;
    private int statusCode;
    private String path;
    private String message;
    private T data;


    public static <T> ApiResponse<T> success(T data, String message, HttpServletRequest request) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message, HttpServletRequest request,int statusCode) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus status, HttpServletRequest request) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(status.value())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
