package com.utpsistemas.distribuidoraavesservice.cobranza.dto;


import java.math.BigDecimal;

public record MovimientoInactivarResponse(
        Long movimientoId,
        Long pedidoId,
        Integer tipo,                  // 1=Descuento, 2=Pago
        char estadoNuevo,              // siempre 'I'
        BigDecimal montoRevertido,
        BigDecimal nuevoTotalPagado,
        BigDecimal nuevoTotalDescuento,
        BigDecimal nuevoSaldo
) { }