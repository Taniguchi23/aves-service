package com.utpsistemas.distribuidoraavesservice.cobranza.dto;

import java.math.BigDecimal;
import java.util.List;

public record PagoResumenResponse(
        BigDecimal totalPagado,
        BigDecimal restante,
        BigDecimal totalPagos,
        BigDecimal totalDescuentos,
        BigDecimal totalExtras,
        List<PagoResponse> listaPagos

) {}