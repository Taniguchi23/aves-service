package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MovimientoMasivoRequest(
        @NotEmpty List<Long> pedidoIds,
        @NotNull Integer tipo,      // 1=Descuento, 2=Pago
        @NotNull Long categoriaId,
        @NotNull @Positive java.math.BigDecimal monto,
        @Size(max = 255) String observacion
) {}