package com.utpsistemas.distribuidoraavesservice.pedido.mapper;

import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteMiniDTO;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaDTO;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Cobranza;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.CobranzaRepository;
import com.utpsistemas.distribuidoraavesservice.estado.dto.EstadoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.DetallePedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioMiniDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
public class PedidoMapper {
    @Autowired
    private DetallePedidoMapper detalleMapper;


    public PedidoResponse toResponse(Pedido pedido, CobranzaDTO cobranzaDTO) {
        EstadoResponse estadoResponse = new EstadoResponse(
                pedido.getEstado().getId(),
                pedido.getEstado().getNombre()
        );

        // mapear usuario mini
        UsuarioMiniDTO usuarioMini = new UsuarioMiniDTO(
                pedido.getUsuario().getId(),
                pedido.getUsuario().getEmail(),
                pedido.getUsuario().getNombres()
        );

        // mapear cliente mini
        ClienteMiniDTO clienteMini = new ClienteMiniDTO(
                pedido.getCliente().getId(),
                pedido.getCliente().getNombres()
        );

        // detalles activos
        List<DetallePedidoResponse> detalleResponses = pedido.getDetalles().stream()
                .filter(d -> d.getEstado() != null && d.getEstado() == 1)
                .map(detalleMapper::toResponse)
                .toList();

        // monto total desde los detalles
        BigDecimal montoTotalEstimado = detalleResponses.stream()
                .map(DetallePedidoResponse::montoEstimado)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PedidoResponse(
                pedido.getId(),
                pedido.getFechaCreacion(),
                pedido.getObservaciones(),
                usuarioMini,
                clienteMini,
                montoTotalEstimado,
                estadoResponse,
                detalleResponses,
                cobranzaDTO
        );
    }
}
