package com.utpsistemas.distribuidoraavesservice.cobranza.service;

import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Pago;

import java.util.List;

public interface PagoService {
    List<Pago> obtenerPagosActivosPorCobranza(Long cobranzaId);
}
