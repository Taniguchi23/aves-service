package com.utpsistemas.distribuidoraavesservice.pedido.mapper;

import com.utpsistemas.distribuidoraavesservice.pedido.dto.DetallePedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PedidoMapper {
    @Autowired
    private DetallePedidoMapper detalleMapper;

    public PedidoResponse toResponse(Pedido pedido) {
        List<DetallePedidoResponse> detalleResponses = pedido.getDetalles().stream()
                .filter(detalle -> detalle.getEstado() == 1)
                .map(detalleMapper::toResponse)
                .toList();

        return new PedidoResponse(
                pedido.getId(),
                pedido.getFechaCreacion(),
                pedido.getObservaciones(),
                pedido.getEstado(),
                pedido.getCliente().getId(),
                pedido.getUsuario().getId(),
                detalleResponses
        );
    }
}
