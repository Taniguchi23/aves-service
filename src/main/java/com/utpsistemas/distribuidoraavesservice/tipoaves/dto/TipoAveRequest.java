package com.utpsistemas.distribuidoraavesservice.tipoaves.dto;

import jakarta.validation.constraints.NotBlank;

public record TipoAveRequest(
        Integer id,
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        float conTripaKg,
        float sinTripaKg,
        float noAplicaKg
) {}
