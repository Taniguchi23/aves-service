package com.utpsistemas.distribuidoraavesservice.pedido.dto;

import com.utpsistemas.distribuidoraavesservice.pedido.enums.TipoMerma;

import java.math.BigDecimal;
public record DetallePedidoResponse(
        Long id,
        Integer tipoAveId,
        String tipoAveNombre,
        Integer cantidad,
        BigDecimal peso,
        BigDecimal precioXKilo,
        BigDecimal montoEstimado,
        float mermaKg,
        TipoMerma tipoMerma,
        boolean opDirecta

) {}