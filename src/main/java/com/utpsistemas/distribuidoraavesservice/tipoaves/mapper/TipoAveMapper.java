package com.utpsistemas.distribuidoraavesservice.tipoaves.mapper;

import com.utpsistemas.distribuidoraavesservice.tipoaves.dto.MermaDTO;
import com.utpsistemas.distribuidoraavesservice.tipoaves.dto.TipoAveRequest;
import com.utpsistemas.distribuidoraavesservice.tipoaves.dto.TipoAveResponse;
import com.utpsistemas.distribuidoraavesservice.tipoaves.entity.TipoAve;
import org.springframework.stereotype.Component;

@Component
public class TipoAveMapper {
    public TipoAve toEntity(TipoAveRequest request) {
        TipoAve ave = new TipoAve();
        ave.setNombre(request.nombre());
        ave.setConTripaKg(request.conTripaKg());
        ave.setSinTripaKg(request.sinTripaKg());
        ave.setNoAplicaKg(request.noAplicaKg());
        return ave;
    }

    public TipoAveResponse toResponse(TipoAve ave) {
        var mermas = java.util.List.of(
                new MermaDTO("CT", ave.getConTripaKg()),
                new MermaDTO("ST", ave.getSinTripaKg()),
                new MermaDTO("NA", ave.getNoAplicaKg())
        );
        return new TipoAveResponse(
                ave.getId(),
                ave.getNombre(),
                mermas
        );
    }
}