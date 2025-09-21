package com.utpsistemas.distribuidoraavesservice.cobranza.service;

import com.utpsistemas.distribuidoraavesservice.cobranza.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface CobranzaMovimientoService {
    MovimientoMasivoResponse  crearMovimientosMasivos(MovimientoMasivoRequest request);
}
