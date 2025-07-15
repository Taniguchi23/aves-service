package com.utpsistemas.distribuidoraavesservice.auth.security;

import com.utpsistemas.distribuidoraavesservice.auth.helper.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ResponseUtil.writeApiResponse(
                response,
                HttpStatus.UNAUTHORIZED,
                request,
                "No estás autenticado. Por favor inicia sesión."
        );
    }
}