package com.utpsistemas.distribuidoraavesservice.pedido.service;

import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoRequest;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;

public interface PedidoService {
    PedidoResponse crearPedido(PedidoRequest request);

}
