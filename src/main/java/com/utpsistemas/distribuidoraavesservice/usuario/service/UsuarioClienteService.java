package com.utpsistemas.distribuidoraavesservice.usuario.service;

import com.utpsistemas.distribuidoraavesservice.usuario.dto.AsignacionClienteRequest;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.AsignacionClienteResponse;

public interface UsuarioClienteService {
    AsignacionClienteResponse asignarClientes(AsignacionClienteRequest request);
}
