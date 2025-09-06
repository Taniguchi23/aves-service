package com.utpsistemas.distribuidoraavesservice.pedido.dto;

import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteMiniDTO;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaResumenDTO;
import com.utpsistemas.distribuidoraavesservice.estado.dto.EstadoDTO;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioMiniDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoListItemDTO(
        Long id,
        ClienteMiniDTO cliente,
        UsuarioMiniDTO usuario,
        LocalDateTime fechaCreacion,
        EstadoDTO estado,
        BigDecimal montoTotalEstimado, // suma de DetallePedido.montoEstimado con estado=1
        CobranzaResumenDTO cobranza
) {}
