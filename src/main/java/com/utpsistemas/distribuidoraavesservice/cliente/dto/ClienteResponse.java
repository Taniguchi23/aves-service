package com.utpsistemas.distribuidoraavesservice.cliente.dto;

public record ClienteResponse (
        Long id,
        String nombres,
        Integer tipoDocumento,
        String numeroDocumento,
        String telefono,
        String direccion,
        String apodo,
        String latitud,
        String longitud,
        Character estado
){}
