package com.utpsistemas.distribuidoraavesservice.usuario.dto;

import java.util.List;

public record AsignacionClienteResponse(
        Long usuarioId,
        String nombres,
        String email,
        List<ClienteAsignado> clientes
) {
    public record ClienteAsignado(
            Long id,
            String nombres,
            String numeroDocumento,
            String telefono,
            String direccion
    ) {}
}