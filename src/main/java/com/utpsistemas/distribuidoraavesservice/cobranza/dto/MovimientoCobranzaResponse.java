package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoCobranzaResponse(
        Long id,
        Integer tipo,
        Long categoriaId,
        String categoriaNombre,
        BigDecimal monto,
        String observacion,
        Character estado
) {}