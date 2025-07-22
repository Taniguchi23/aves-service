package com.utpsistemas.distribuidoraavesservice.pedido.service;

import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoRequest;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;

import java.util.List;

public interface PedidoService {
    PedidoResponse crearPedido(PedidoRequest request);
    List<PedidoResponse> listarPedidosPorCliente(Long clienteId);
    PedidoResponse actualizarPedido(PedidoRequest pedidoRequest);

}
