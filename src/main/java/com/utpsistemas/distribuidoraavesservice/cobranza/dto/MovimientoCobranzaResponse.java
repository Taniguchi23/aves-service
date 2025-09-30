package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoCobranzaResponse(
        Long id,
        Integer tipo,
        Long categoriaId,
        String categoriaNombre,
        BigDecimal monto,
        String observacion,
        Character estado,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCreacion,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaActualizacion
) {}