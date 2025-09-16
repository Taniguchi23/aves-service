package com.utpsistemas.distribuidoraavesservice.cobranza.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CobranzaClienteResumenProjection {
    Long getClienteId();
    String getClienteNombre();
    BigDecimal getImporteTotal();
    Long getCantidadPedidos();
    Long getEstadoId();
    String getEstadoNombre();
    LocalDateTime getFechaMax();
}