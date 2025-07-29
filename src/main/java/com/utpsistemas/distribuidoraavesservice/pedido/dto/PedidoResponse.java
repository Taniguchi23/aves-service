package com.utpsistemas.distribuidoraavesservice.pedido.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        LocalDateTime fechaCreacion,
        String observaciones,
        String estado,
        Long clienteId,
        Long usuarioId,
        List<DetallePedidoResponse> detalles
) {}