package com.utpsistemas.distribuidoraavesservice.usuario.service;

import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioRequest;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioResponse;

import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> listarUsuarios();
    UsuarioResponse obtenerPorId(Long id);
    UsuarioResponse crearUsuario(UsuarioRequest request);
    UsuarioResponse actualizarUsuario(UsuarioRequest request);
}
