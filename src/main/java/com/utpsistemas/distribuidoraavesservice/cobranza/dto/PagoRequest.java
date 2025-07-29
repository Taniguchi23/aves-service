package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;


public record PagoRequest(

        @NotNull(message = "El ID de la cobranza es obligatorio.")
        Long cobranzaId,

        @NotNull(message = "El tipo de pago es obligatorio.")
        Long tipoPagoId,


        Long formaPagoId,

        @NotNull(message = "El monto es obligatorio.")
        @Positive(message = "El monto debe ser mayor que cero.")
        BigDecimal monto,

        String motivo,

        @NotNull(message = "La fecha del pago es obligatoria.")
        LocalDate fecha

) {}