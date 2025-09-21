package com.utpsistemas.distribuidoraavesservice.cobranza.mapper;


import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteMiniDTO;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaPedidoResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.MovimientoCobranzaResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.PedidoCobranzaMovimiento;
import com.utpsistemas.distribuidoraavesservice.cobranza.mapper.CobranzaMovimientoMapper;
import com.utpsistemas.distribuidoraavesservice.estado.dto.EstadoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.DetallePedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import com.utpsistemas.distribuidoraavesservice.pedido.mapper.DetallePedidoMapper;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioMiniDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CobranzaPedidoMapper {
    @Autowired
    private DetallePedidoMapper detalleMapper;

    @Autowired
    private CobranzaMovimientoMapper pedidoCobranzaMapper;


    public CobranzaPedidoMapper(DetallePedidoMapper detalleMapper,
                        CobranzaMovimientoMapper pedidoCobranzaMapper) {
        this.detalleMapper = detalleMapper;
        this.pedidoCobranzaMapper = pedidoCobranzaMapper;
    }
    public CobranzaPedidoResponse toResponse(Pedido pedido,
                                     List<PedidoCobranzaMovimiento> movimientos) {
        EstadoResponse estadoResponse = new EstadoResponse(
                pedido.getEstado().getId(),
                pedido.getEstado().getNombre()
        );
        /*
        UsuarioMiniDTO usuarioMini = new UsuarioMiniDTO(
                pedido.getUsuario().getId(),
                pedido.getUsuario().getEmail(),
                pedido.getUsuario().getNombres()
        );

        // mapear cliente mini
        ClienteMiniDTO clienteMini = new ClienteMiniDTO(
                pedido.getCliente().getId(),
                pedido.getCliente().getNombres()
        );*/

        // detalles activos
        List<DetallePedidoResponse> detalleResponses = pedido.getDetalles().stream()
                .filter(d -> d.getEstado() != null && d.getEstado() == 1)
                .map(detalleMapper::toResponse)
                .toList();

        var movimientosResponses = (movimientos == null)
                ? List.<MovimientoCobranzaResponse>of()
                : movimientos.stream()
                .map(pedidoCobranzaMapper::toResponse) // sin filtrar, sin ordenar
                .toList();

/*
        var total = nz(pedido.getTotalImporte());

        var totalPagado = (movimientos == null) ? java.math.BigDecimal.ZERO
                : movimientos.stream()
                .filter(m -> m != null && java.util.Objects.equals(m.getTipo(), 2)) // 2 = PAGO
                .map(PedidoCobranzaMovimiento::getMonto)
                .map(this::nz)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        var totalDescuento = (movimientos == null) ? java.math.BigDecimal.ZERO
                : movimientos.stream()
                .filter(m -> m != null && java.util.Objects.equals(m.getTipo(), 1)) // 1 = DESCUENTO
                .map(PedidoCobranzaMovimiento::getMonto)
                .map(this::nz)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        var totalSaldo = total.subtract(totalPagado).subtract(totalDescuento);
*/
        var cantidadDetalles = (pedido.getCantidadDetalles() != null)
                ? pedido.getCantidadDetalles()
                : (pedido.getDetalles() != null ? pedido.getDetalles().size() : 0);


        return new CobranzaPedidoResponse(
                pedido.getId(),
                pedido.getFechaCreacion(),
                pedido.getObservaciones(),
                estadoResponse,
                pedido.getTotalImporte(),
                cantidadDetalles,
                detalleResponses,
                movimientosResponses,
                pedido.getTotalPagado(),
                pedido.getTotalDescuento(),
                pedido.getTotalSaldo()
        );
    }

    public CobranzaPedidoResponse toResponse(Pedido pedido) {
        return toResponse(pedido, List.of());
    }
    private java.math.BigDecimal nz(java.math.BigDecimal v) {
        return v == null ? java.math.BigDecimal.ZERO : v;
    }

}
