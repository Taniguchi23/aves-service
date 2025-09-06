package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CobranzaDTO (
        Long id,
        String estado,
        BigDecimal montoTotal,
        BigDecimal pagado,
        BigDecimal saldo
) {
}
