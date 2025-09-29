package com.utpsistemas.distribuidoraavesservice.cobranza.service;

import com.utpsistemas.distribuidoraavesservice.cobranza.dto.*;

public interface CobranzaMovimientoService {
    MovimientoMasivoResponse  crearMovimientosMasivos(MovimientoMasivoRequest request);

    MovimientoInactivarResponse inactivarMovimiento(Long movimientoId);

}
