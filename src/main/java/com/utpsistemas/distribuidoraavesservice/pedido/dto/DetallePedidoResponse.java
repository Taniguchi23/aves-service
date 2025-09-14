package com.utpsistemas.distribuidoraavesservice.pedido.dto;

import java.math.BigDecimal;
public record DetallePedidoResponse(
        Long id,
        Integer tipoAveId,
        String tipoAveNombre,
        Integer cantidad,
        BigDecimal peso,
        BigDecimal precioXKilo,
        BigDecimal mermaKg,
        String tipoMerma,
        Boolean opDirecta

) {}