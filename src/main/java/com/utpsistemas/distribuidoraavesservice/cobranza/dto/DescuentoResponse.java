package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import java.math.BigDecimal;

public record DescuentoResponse(
        Long id,
        Long cobranzaId,
        BigDecimal monto,
        String motivo
) {}
