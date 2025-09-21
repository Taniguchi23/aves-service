package com.utpsistemas.distribuidoraavesservice.cobranza.mapper;

import com.utpsistemas.distribuidoraavesservice.cobranza.dto.MovimientoCobranzaResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.PedidoCobranzaMovimiento;
import org.springframework.stereotype.Component;

@Component
public class MovimientoCobranzaMapper {
    public MovimientoCobranzaResponse toResponse(PedidoCobranzaMovimiento m) {
        if (m == null) return null;
        return new MovimientoCobranzaResponse(
                m.getId(),
                m.getTipo(), // Integer 1/2
                m.getCategoria() != null ? m.getCategoria().getId() : null,
                m.getCategoria() != null ? m.getCategoria().getNombre() : null,
                m.getMonto(),
                m.getObservacion(),
                m.getEstado()
        );
    }
}