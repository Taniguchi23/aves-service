package com.utpsistemas.distribuidoraavesservice.pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoRequest(
        Long id,
        @NotNull(message = "El id del cliente es obligatorio")
        Long clienteId,
        String observaciones,
        @NotEmpty(message = "Debe incluir al menos un detalle de pedido")
        @Valid
        List<DetallePedidoRequest> detallePedido,
        Long estadoId
) {}
