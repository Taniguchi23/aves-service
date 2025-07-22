package com.utpsistemas.distribuidoraavesservice.tipoaves.service;

import com.utpsistemas.distribuidoraavesservice.tipoaves.dto.TipoAveRequest;
import com.utpsistemas.distribuidoraavesservice.tipoaves.dto.TipoAveResponse;
import com.utpsistemas.distribuidoraavesservice.tipoaves.entity.TipoAve;

import java.util.List;

public interface TipoAveService {
    List<TipoAveResponse> listarTipoAve();
    TipoAveResponse guardarTipoAve(TipoAveRequest tipoAveRequest);
    TipoAveResponse actualizarTipoAve(TipoAveRequest tipoAveRequest);
}
