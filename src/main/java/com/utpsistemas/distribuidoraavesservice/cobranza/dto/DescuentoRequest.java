package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DescuentoRequest(
        @NotNull(message = "El ID de la cobranza es obligatorio")
        Long cobranzaId,
        @NotNull(message = "El monto del descuento es obligatorio")
        BigDecimal monto,

        String motivo
) {}
