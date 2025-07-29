package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Cobranza;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CobranzaRefreshResponse(
        CobranzaResponse cobranza,
        PagoResumenResponse pagoResumen
) {
}
