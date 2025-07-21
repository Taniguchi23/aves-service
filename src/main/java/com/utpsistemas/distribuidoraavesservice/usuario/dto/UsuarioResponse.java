package com.utpsistemas.distribuidoraavesservice.usuario.dto;

import java.util.List;

public record UsuarioResponse(
        Long id,
        String nombres,
        String email,
        Character estado,
        List<RolDto> roles
) {
    public record RolDto(Long id, String nombre) {}
}

