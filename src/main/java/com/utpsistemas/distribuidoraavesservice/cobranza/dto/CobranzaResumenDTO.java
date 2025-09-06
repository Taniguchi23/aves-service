package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import java.math.BigDecimal;

public record CobranzaResumenDTO(
        Long cobranzaId,
        String estado,          // Pendiente, Parcial, Pagado (tabla cobranza)
        BigDecimal montoTotal,
        BigDecimal montoPagado,
        BigDecimal saldo
) {}
