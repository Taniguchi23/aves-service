package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteMiniDTO;
import com.utpsistemas.distribuidoraavesservice.estado.dto.EstadoResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CobranzaClienteResumenResponse(
        Long id,
        ClienteMiniDTO cliente,
        BigDecimal importeTotal,
        Integer cantidadDetalles,
        EstadoResponse estado,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDateTime fecha) {}