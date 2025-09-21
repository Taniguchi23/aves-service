package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record PedidoCobranzaMovimientoRequest(
        @NotEmpty List<Long> pedidoIds,
        @NotNull Integer tipo,          // 1 = Descuento, 2 = Pago
        @NotNull Long categoriaId,
        @NotNull @Positive BigDecimal monto,
        @Size(max = 255) String observacion
) {}