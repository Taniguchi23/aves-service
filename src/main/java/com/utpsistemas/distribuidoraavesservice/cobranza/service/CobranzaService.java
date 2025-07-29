package com.utpsistemas.distribuidoraavesservice.cobranza.service;

import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaRefreshRequest;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaRefreshResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaRequest;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.FormaPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.TipoPago;

import java.util.List;

public interface CobranzaService {
    CobranzaResponse crearCobranza(CobranzaRequest request);
    CobranzaRefreshResponse refrescarCobranza(CobranzaRefreshRequest request);
    List<CobranzaRefreshResponse> listarCobranzasAsignadas();


    List<FormaPago> listarFormaPagos();
    List<TipoPago> listarTipoPagos();
}
