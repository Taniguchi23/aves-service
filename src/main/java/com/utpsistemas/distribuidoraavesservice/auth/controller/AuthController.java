package com.utpsistemas.distribuidoraavesservice.auth.controller;

import com.utpsistemas.distribuidoraavesservice.auth.dto.LoginRequest;
import com.utpsistemas.distribuidoraavesservice.auth.helper.ApiResponse;
import com.utpsistemas.distribuidoraavesservice.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aves-service/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private  AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request),"Login exitoso", httpRequest));
    }
}
