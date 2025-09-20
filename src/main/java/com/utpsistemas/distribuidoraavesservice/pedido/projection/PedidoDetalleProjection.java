package com.utpsistemas.distribuidoraavesservice.pedido.projection;


import java.math.BigDecimal;

public interface PedidoDetalleProjection {
    Long getPedidoId();
    Long getCobranzaId();
    String getCobranzaEstado();
    BigDecimal getTotal();
    BigDecimal getPagado();
}
