package com.utpsistemas.distribuidoraavesservice.cliente.controller;

import com.utpsistemas.distribuidoraavesservice.auth.helper.ApiResponse;
import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteRequest;
import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteResponse;
import com.utpsistemas.distribuidoraavesservice.cliente.entity.Cliente;
import com.utpsistemas.distribuidoraavesservice.cliente.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("aves-service/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PreAuthorize("hasRole('Distribuidor')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> listarClientes( HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.listarClientesActivos(),"Lista de clientes", httpRequest));
    }

    @PreAuthorize("hasRole('Distribuidor')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponse>> obtenerCliente(@PathVariable Long id, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.obtenerPorId(id),"Cliente", httpRequest));
    }

    @PreAuthorize("hasRole('Distribuidor')")
    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponse>> crearCliente(@RequestBody ClienteRequest clienteRequest, HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(clienteService.crearCliente(clienteRequest),"Cliente creado exitosamente", httpRequest));
    }

    @PreAuthorize("hasRole('Distribuidor')")
    @PutMapping()
    public ResponseEntity<ApiResponse<ClienteResponse>> actualizarCliente( @RequestBody ClienteRequest clienteRequest, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.actualizarCliente(clienteRequest),"Cliente actualizado", httpRequest));
    }


}
