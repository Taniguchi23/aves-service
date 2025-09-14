package com.utpsistemas.distribuidoraavesservice.tipoaves.dto;

import java.util.List;

public record TipoAveResponse(
        Integer id,
        String nombre,
        List<MermaDTO> mermas
) {}