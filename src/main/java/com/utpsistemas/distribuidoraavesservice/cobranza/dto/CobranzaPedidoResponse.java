package com.utpsistemas.distribuidoraavesservice.cobranza.dto;


import com.utpsistemas.distribuidoraavesservice.estado.dto.EstadoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.DetallePedidoResponse;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CobranzaPedidoResponse(
        Long id,
        LocalDateTime fechaCreacion,
        String observaciones,
        EstadoResponse estado,
        BigDecimal totalImporte,
        Integer cantidadDetalles,
        List<DetallePedidoResponse> detalles,
        List<MovimientoCobranzaResponse> movimientos,
        BigDecimal totalPagado,
        BigDecimal totalDescuento,
        BigDecimal totalSaldo
) {}