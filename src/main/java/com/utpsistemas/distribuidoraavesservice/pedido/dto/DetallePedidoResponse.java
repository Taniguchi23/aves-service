package com.utpsistemas.distribuidoraavesservice.pedido.dto;

import java.math.BigDecimal;
public record DetallePedidoResponse(
        Long id,
        Integer tipoAveId,
        String tipoAveNombre,
        Integer cantidad,
        BigDecimal pesoBase,
        BigDecimal peso,
        BigDecimal precioXKilo,
        BigDecimal mermaKg,
        BigDecimal importeSubTotal,
        String tipoMerma,
        Boolean opDirecta

) {}