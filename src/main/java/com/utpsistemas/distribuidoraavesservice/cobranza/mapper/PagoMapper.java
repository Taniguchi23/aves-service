package com.utpsistemas.distribuidoraavesservice.cobranza.mapper;

import com.utpsistemas.distribuidoraavesservice.cobranza.dto.PagoResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Pago;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {
    public PagoResponse toResponse(Pago pago) {
        return new PagoResponse(
                pago.getId(),
                pago.getCobranza().getId(),
                pago.getTipoPago().getNombre(),
                pago.getFormaPago() != null ? pago.getFormaPago().getNombre() : null,
                pago.getMonto(),
                pago.getMotivo(),
                pago.getFecha(),
                pago.getEstado()
        );
    }
}
