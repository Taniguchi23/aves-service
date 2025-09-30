package com.utpsistemas.distribuidoraavesservice.pedido.mapper;

import com.utpsistemas.distribuidoraavesservice.pedido.dto.DetallePedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.PedidoDetalle;
import org.springframework.stereotype.Component;

@Component
public class DetallePedidoMapper {
    public DetallePedidoResponse toResponse(PedidoDetalle detalle) {
        return new DetallePedidoResponse(
                detalle.getId(),
                detalle.getTipoAve().getId(),
                detalle.getTipoAve().getNombre(),
                detalle.getCantidad(),
                detalle.getPesoBase(),
                detalle.getPeso(),
                detalle.getPrecioXKilo(),
                detalle.getMermaKg(),
                detalle.getImporteSubTotal(),
                detalle.getTipoMerma(),
                detalle.getOpDirecta()
        );
    }
}
