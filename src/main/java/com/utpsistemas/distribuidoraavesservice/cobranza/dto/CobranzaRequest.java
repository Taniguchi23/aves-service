package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import jakarta.validation.constraints.NotNull;

public record CobranzaRequest(
        @NotNull(message = "El ID del pedido es obligatorio")
        Long pedidoId
) {
}
