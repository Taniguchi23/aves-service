package com.utpsistemas.distribuidoraavesservice.cobranza.service;

import com.utpsistemas.distribuidoraavesservice.cobranza.dto.PagoRequest;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.PagoResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Pago;

import java.util.List;

public interface PagoService {
    PagoResponse crearPago(PagoRequest pagoRequest);
    List<Pago> obtenerPagosActivosPorCobranza(Long cobranzaId);
}
