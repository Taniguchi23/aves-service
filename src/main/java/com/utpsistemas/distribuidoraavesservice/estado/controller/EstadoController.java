package com.utpsistemas.distribuidoraavesservice.estado.controller;

import com.utpsistemas.distribuidoraavesservice.auth.helper.ApiResponse;
import com.utpsistemas.distribuidoraavesservice.estado.dto.EstadoResponse;
import com.utpsistemas.distribuidoraavesservice.estado.service.EstadoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("aves-service/estado")
public class EstadoController {
    @Autowired
    private EstadoService estadoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EstadoResponse>>> listaEstado(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.success(estadoService.listaEstado(),"Lista de estado",httpRequest));
    }
}
