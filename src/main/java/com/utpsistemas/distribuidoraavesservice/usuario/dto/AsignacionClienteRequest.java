package com.utpsistemas.distribuidoraavesservice.usuario.dto;

import java.util.List;

public record AsignacionClienteRequest(
        Long usuarioId,
        List<Long> clienteIds
) {}
