package com.utpsistemas.distribuidoraavesservice.usuario.dto;

import java.util.List;

public record UsuarioProfile(
        Long id,
        String nombres,
        String email,
        Character estado
) {}
