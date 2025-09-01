package com.utpsistemas.distribuidoraavesservice.auth.dto;

import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioProfile;

import java.util.List;

public record LoginResponse (
         String token,
         List<String> roles,
         UsuarioProfile profile
){}
