package com.utpsistemas.distribuidoraavesservice.cliente.dto;

import jakarta.validation.constraints.NotBlank;

public record ClienteRequest (
        Long id,
        @NotBlank(message = "El nombre es requerido")
        String nombres,
        Integer tipoDocumento,
        @NotBlank(message = "El documento es requerido")
        String numeroDocumento,
        String telefono,
        String direccion,
        String apodo,
        String latitud,
        String longitud,
        Character estado
){}
