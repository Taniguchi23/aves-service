package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import java.math.BigDecimal;
import java.util.List;

public record MovimientoMasivoResponse(
        Integer tipo,
        Long categoriaId,
        BigDecimal montoSolicitado,
        BigDecimal montoAplicado,
        BigDecimal montoRestante,
        BigDecimal saldoRestante,
        List<MovimientoAplicadoItem> detalles
) {}