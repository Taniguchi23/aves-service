package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import java.math.BigDecimal;

public record MovimientoAplicadoItem(
        Long pedidoId,
        Long movimientoId,
        BigDecimal aplicado,
        BigDecimal nuevoTotalPagado,
        BigDecimal nuevoTotalDescuento,
        BigDecimal nuevoSaldo
) {}