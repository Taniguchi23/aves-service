package com.utpsistemas.distribuidoraavesservice.usuario.controller;

import com.utpsistemas.distribuidoraavesservice.auth.helper.ApiResponse;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.AsignacionClienteRequest;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.AsignacionClienteResponse;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioRequest;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioResponse;
import com.utpsistemas.distribuidoraavesservice.usuario.service.UsuarioClienteService;
import com.utpsistemas.distribuidoraavesservice.usuario.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("aves-service/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioClienteService usuarioClienteService;

    @PreAuthorize("hasRole('Distribuidor')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> listar(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.listarUsuarios(),"Lista de usuarios", httpRequest));
    }

    @PreAuthorize("hasRole('Distribuidor')")
    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> crearUsuario(@RequestBody UsuarioRequest usuarioRequest, HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(usuarioService.crearUsuario(usuarioRequest),"Usuario creado exitosamente", httpRequest));
    }

    @PreAuthorize("hasRole('Distribuidor')")
    @PutMapping()
    public ResponseEntity<ApiResponse<UsuarioResponse>> actualizarUsuario(@RequestBody UsuarioRequest usuarioRequest, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.actualizarUsuario(usuarioRequest),"Usuario actualizado", httpRequest));
    }

    @PreAuthorize("hasRole('Distribuidor')")
    @PostMapping("asignar-cliente")
    public ResponseEntity<ApiResponse<AsignacionClienteResponse>> actualizarUsuarioCliente(@RequestBody AsignacionClienteRequest asignacionClienteRequest, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.success(usuarioClienteService.asignarClientes(asignacionClienteRequest),"Lista de clientes actualizado", httpRequest));
    }
}
