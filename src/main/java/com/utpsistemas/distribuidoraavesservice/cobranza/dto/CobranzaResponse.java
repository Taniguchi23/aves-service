package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CobranzaResponse(
        Long id,
        Long pedidoId,
        BigDecimal montoTotal,
        LocalDate fecha,
        String estado,
        PedidoResponse pedido
) {
}
