package com.utpsistemas.distribuidoraavesservice.cobranza.mapper;

import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Cobranza;
import com.utpsistemas.distribuidoraavesservice.pedido.mapper.PedidoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CobranzaMapper {

    @Autowired
    private PedidoMapper pedidoMapper;

    public CobranzaResponse toResponse(Cobranza cobranza) {
        return new CobranzaResponse(
                cobranza.getId(),
                cobranza.getPedido().getId(),
                cobranza.getMontoTotal(),
                cobranza.getFecha(),
                cobranza.getEstado(),
                pedidoMapper.toResponse(cobranza.getPedido())
        );
    }
}
