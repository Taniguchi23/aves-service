package com.utpsistemas.distribuidoraavesservice.cobranza.controller;

import com.utpsistemas.distribuidoraavesservice.auth.helper.ApiResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.*;
import com.utpsistemas.distribuidoraavesservice.cobranza.service.CobranzaService;
import com.utpsistemas.distribuidoraavesservice.cobranza.service.PagoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("aves-service/cobranza")
public class CobranzaController {
    @Autowired
    private CobranzaService cobranzaService;

    @Autowired
    private PagoService pagoService;

    @PostMapping
    public ResponseEntity<ApiResponse<CobranzaResponse>> crearCobranza(@Valid @RequestBody CobranzaRequest cobranzaRequest, HttpServletRequest https) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(cobranzaService.crearCobranza(cobranzaRequest),"Cobranza creada", https));
    }

    @PostMapping("pago")
    public ResponseEntity<ApiResponse<PagoResponse>> crearPago(@Valid @RequestBody PagoRequest pagoRequest, HttpServletRequest https) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(pagoService.crearPago(pagoRequest),"Cobranza creada", https));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<CobranzaRefreshResponse>> refrescarCobranza(@Valid @RequestBody CobranzaRefreshRequest cobranzaRequest, HttpServletRequest https) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(cobranzaService.refrescarCobranza(cobranzaRequest),"Cobranza actualizada", https));
    }
}
