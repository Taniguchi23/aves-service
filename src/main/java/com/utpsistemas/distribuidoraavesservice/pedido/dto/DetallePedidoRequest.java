package com.utpsistemas.distribuidoraavesservice.pedido.dto;


import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DetallePedidoRequest(
        Long id,
        @NotNull(message = "Se require el id tipo de aver")
        Integer tipoAveId,
        @NotNull(message = "Se requiere la cantidad de ave")
        Integer cantidad,
        BigDecimal peso,
        BigDecimal precioXKilo,
        @NotNull(message = "Se requiere la merma")
        float mermaKg,
        String tipoMerma,
        boolean opDirecta
) {}
