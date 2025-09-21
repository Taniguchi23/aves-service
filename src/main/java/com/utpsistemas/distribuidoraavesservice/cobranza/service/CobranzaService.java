package com.utpsistemas.distribuidoraavesservice.cobranza.service;

import com.utpsistemas.distribuidoraavesservice.cobranza.dto.*;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.FormaPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.TipoPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.projection.CobranzaClienteResumenProjection;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;

import java.util.List;

public interface CobranzaService {
    CobranzaResponse crearCobranza(CobranzaRequest request);
    CobranzaRefreshResponse refrescarCobranza(CobranzaRefreshRequest request);
    List<CobranzaRefreshResponse> listarCobranzasAsignadas();


    List<FormaPago> listarFormaPagos();
    List<TipoPago> listarTipoPagos();

    List<CobranzaClienteResumenResponse> listarResumenCobranzasPorUsuario(Long usuarioId);

    List<CobranzaPedidoResponse> listarCobranzaPorUsuario(Long usuarioId);

    CobranzaPedidoResponse cobranzaPedidoPorId(Long usuarioId, Long pedidoId);
}
