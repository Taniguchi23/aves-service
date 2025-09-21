package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import com.utpsistemas.distribuidoraavesservice.cobranza.enums.TipoPedidoCobranzaMovimiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoCobranzaMovimientoResponse(
        Long id,
        Long pedidoId,
        Integer tipo,
        Long categoriaId,
        String categoriaNombre,
        BigDecimal monto,
        String observacion,
        Character estado
) {}