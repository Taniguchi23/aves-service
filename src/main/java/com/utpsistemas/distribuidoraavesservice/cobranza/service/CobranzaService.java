package com.utpsistemas.distribuidoraavesservice.cobranza.service;

import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaRefreshRequest;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaRefreshResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaRequest;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaResponse;

public interface CobranzaService {
    CobranzaResponse crearCobranza(CobranzaRequest request);
    CobranzaRefreshResponse refrescarCobranza(CobranzaRefreshRequest request);
}
