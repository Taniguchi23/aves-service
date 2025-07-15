package com.utpsistemas.distribuidoraavesservice.cliente.service;

import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteRequest;
import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteResponse;
import com.utpsistemas.distribuidoraavesservice.cliente.entity.Cliente;

import java.util.List;

public interface ClienteService {
    List<ClienteResponse> listarClientesActivos();
    ClienteResponse obtenerPorId(String id);
    ClienteResponse crearCliente(ClienteRequest request);
    ClienteResponse actualizarCliente(ClienteRequest request);
}
