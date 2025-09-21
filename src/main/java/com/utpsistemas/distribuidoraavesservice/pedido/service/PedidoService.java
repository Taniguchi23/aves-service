package com.utpsistemas.distribuidoraavesservice.pedido.service;

import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoListItemDTO;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoRequest;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;

import java.util.List;

public interface PedidoService {
    PedidoResponse crearPedido(PedidoRequest request);
    List<PedidoResponse> listarPedidosAsignadosAlUsuario();
    PedidoResponse actualizarPedido(PedidoRequest pedidoRequest);
    boolean validarAsignacionCliente(Long usuarioId, Long clienteId);

    PedidoResponse confirmarPedido(Long id);
    List<PedidoResponse> pedidosPorUsuarioId(Long id);


    PedidoResponse pedidosPorId(Long id);
}
