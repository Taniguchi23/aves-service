package com.utpsistemas.distribuidoraavesservice.auth.service;

import com.utpsistemas.distribuidoraavesservice.auth.dto.LoginRequest;
import com.utpsistemas.distribuidoraavesservice.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
