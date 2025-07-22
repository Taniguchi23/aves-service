package com.utpsistemas.distribuidoraavesservice.tipoaves.controller;

import com.utpsistemas.distribuidoraavesservice.auth.helper.ApiResponse;
import com.utpsistemas.distribuidoraavesservice.tipoaves.dto.TipoAveRequest;
import com.utpsistemas.distribuidoraavesservice.tipoaves.dto.TipoAveResponse;
import com.utpsistemas.distribuidoraavesservice.tipoaves.service.TipoAveService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("aves-service/tipos-aves")
public class TipoAveController {
    @Autowired
    private TipoAveService tipoAveService;

    @PreAuthorize("hasRole('Distribuidor')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<TipoAveResponse>>> listarTipoAve(HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.success(tipoAveService.listarTipoAve(),"Lista de tipos aves", httpRequest));
    }

    @PreAuthorize("hasRole('Distribuidor')")
    @PostMapping
    public ResponseEntity<ApiResponse<TipoAveResponse>> crearTipoAve(@RequestBody TipoAveRequest tipoAveRequest, HttpServletRequest httpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(tipoAveService.guardarTipoAve(tipoAveRequest),"Tipo de ave guardado", httpRequest));
    }

    @PreAuthorize("hasRole('Distribuidor')")
    @PutMapping
    public ResponseEntity<ApiResponse<TipoAveResponse>> actualizarTipoAve(@RequestBody TipoAveRequest tipoAveRequest, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(ApiResponse.success(tipoAveService.actualizarTipoAve(tipoAveRequest),"Tipo de ave actualizado", httpRequest));
    }
}
