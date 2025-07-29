package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoResponse(
        Long id,
        Long cobranzaId,
        String tipoPago,
        String formaPago,
        BigDecimal monto,
        String motivo,
        LocalDate fecha,
        Character estado
) {}