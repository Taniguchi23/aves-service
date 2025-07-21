package com.utpsistemas.distribuidoraavesservice.usuario.dto;

import java.util.List;

public record UsuarioRequest(
        Long id,
        String nombres,
        String email,
        String password,
        Character estado,
        List<Long> rolesIds
) {}
