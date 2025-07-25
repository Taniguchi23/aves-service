package com.utpsistemas.distribuidoraavesservice.pedido.controller;

import com.utpsistemas.distribuidoraavesservice.auth.helper.ApiResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoRequest;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.service.PedidoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("aves-service/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @PreAuthorize("hasRole('Vendedor')")
    @PostMapping
    public ResponseEntity<ApiResponse<PedidoResponse>> crearPedido(@RequestBody PedidoRequest pedidoRequest , HttpServletRequest httpStatus){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(pedidoService.crearPedido(pedidoRequest),"Pedido creado", httpStatus));
    }

    @PreAuthorize("hasRole('Vendedor')")
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<List<PedidoResponse>>> listarPedidosPorCliente(@PathVariable Long id, HttpServletRequest httpStatus){
        return ResponseEntity.ok(ApiResponse.success(pedidoService.listarPedidosPorCliente(id),"Pedido listado", httpStatus));
    }

    @PreAuthorize("hasRole('Vendedor')")
    @PutMapping
    public ResponseEntity<ApiResponse<PedidoResponse>> actualizarPedido(@RequestBody PedidoRequest pedidoRequest, HttpServletRequest httpStatus){
        return ResponseEntity.ok(ApiResponse.success(pedidoService.actualizarPedido(pedidoRequest),"Pedido actualizado", httpStatus));
    }

}
