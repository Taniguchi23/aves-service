package com.utpsistemas.distribuidoraavesservice.pedido.mapper;

import com.utpsistemas.distribuidoraavesservice.pedido.dto.DetallePedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.DetallePedido;
import org.springframework.stereotype.Component;

@Component
public class DetallePedidoMapper {
    public DetallePedidoResponse toResponse(DetallePedido detalle) {
        return new DetallePedidoResponse(
                detalle.getId(),
                detalle.getTipoAve().getId(),
                detalle.getTipoAve().getNombre(),
                detalle.getCantidadPollo(),
                detalle.getPeso(),
                detalle.getPrecioXKilo(),
                detalle.getMontoEstimado(),
                detalle.getMermaKg(),
                detalle.getTipoMerma(),
                detalle.getOpDirecta()
        );
    }
}
