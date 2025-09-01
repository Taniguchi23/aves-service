package com.utpsistemas.distribuidoraavesservice.estado.mapper;

import com.utpsistemas.distribuidoraavesservice.estado.dto.EstadoResponse;
import com.utpsistemas.distribuidoraavesservice.estado.entity.Estado;
import org.springframework.stereotype.Component;

@Component
public class EstadoMapper {
    public EstadoResponse toEstadoResponse(Estado estado) {
        return new EstadoResponse(
                estado.getId(),
                estado.getNombre()
        );
    }
}
