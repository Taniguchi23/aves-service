package com.utpsistemas.distribuidoraavesservice.auth.dto;

import java.util.List;

public record LoginResponse (
         String token,
         List<String> roles
){}
