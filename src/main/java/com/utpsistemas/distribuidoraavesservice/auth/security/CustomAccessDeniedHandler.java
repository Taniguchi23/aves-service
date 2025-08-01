package com.utpsistemas.distribuidoraavesservice.auth.security;

import com.utpsistemas.distribuidoraavesservice.auth.helper.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.error("Llego access denied");
        ResponseUtil.writeApiResponse(
                response,
                HttpStatus.BAD_REQUEST,
                request,
                "No tienes permisos para acceder a este recurso."
        );
    }
}
