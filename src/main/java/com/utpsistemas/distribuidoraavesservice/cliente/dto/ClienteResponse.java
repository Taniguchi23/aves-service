package com.utpsistemas.distribuidoraavesservice.cliente.dto;

public record ClienteResponse (
        String id,
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
