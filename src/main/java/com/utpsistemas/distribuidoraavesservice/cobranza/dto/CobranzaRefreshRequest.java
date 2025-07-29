package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import jakarta.validation.constraints.NotNull;

public record CobranzaRefreshRequest(
        @NotNull(message = "El ID de cobranza es obligatorio")
        Long cobranzaId
) {
}
