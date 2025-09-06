package com.utpsistemas.distribuidoraavesservice.pedido.dto;

import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteMiniDTO;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaDTO;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaResponse;
import com.utpsistemas.distribuidoraavesservice.estado.dto.EstadoResponse;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioMiniDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        LocalDateTime fechaCreacion,
        String observaciones,
        UsuarioMiniDTO usuarioMini,
        ClienteMiniDTO clienteMini,
        BigDecimal montoTotalEstimado,
        EstadoResponse estado,
        List<DetallePedidoResponse> detalles,
        CobranzaDTO cobranza
) {}