package com.utpsistemas.distribuidoraavesservice.cobranza.controller;

import com.utpsistemas.distribuidoraavesservice.auth.helper.ApiResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.*;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.FormaPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.TipoPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.service.CobranzaService;
import com.utpsistemas.distribuidoraavesservice.cobranza.service.PagoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("aves-service/cobranza")
public class CobranzaController {
    @Autowired
    private CobranzaService cobranzaService;

    @Autowired
    private PagoService pagoService;

    @PreAuthorize("hasRole('Cobrador')")
    @PostMapping
    public ResponseEntity<ApiResponse<CobranzaResponse>> crearCobranza(@Valid @RequestBody CobranzaRequest cobranzaRequest, HttpServletRequest https) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(cobranzaService.crearCobranza(cobranzaRequest),"Cobranza creada", https));
    }

    @PreAuthorize("hasRole('Cobrador')")
    @PostMapping("pago")
    public ResponseEntity<ApiResponse<PagoResponse>> crearPago(@Valid @RequestBody PagoRequest pagoRequest, HttpServletRequest https) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(pagoService.crearPago(pagoRequest),"Cobranza creada", https));
    }

    @PreAuthorize("hasRole('Cobrador')")
    @PutMapping
    public ResponseEntity<ApiResponse<CobranzaRefreshResponse>> refrescarCobranza(@Valid @RequestBody CobranzaRefreshRequest cobranzaRequest, HttpServletRequest https) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(cobranzaService.refrescarCobranza(cobranzaRequest),"Cobranza actualizada", https));
    }

    @PreAuthorize("hasRole('Cobrador')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CobranzaRefreshResponse>>> listaCobranzas(HttpServletRequest https){
        return  ResponseEntity.ok(ApiResponse.success(cobranzaService.listarCobranzasAsignadas(),"Lista de cobranzas", https));
    }

    @GetMapping("forma-pago")
    public ResponseEntity<ApiResponse<List<FormaPago>>> listarFormaPago(HttpServletRequest https){
        return ResponseEntity.ok(ApiResponse.success(cobranzaService.listarFormaPagos(),"Listar forma de pago", https));
    }

    @GetMapping("tipo-pago")
    public ResponseEntity<ApiResponse<List<TipoPago>>> listarTipoPago(HttpServletRequest https){
        return ResponseEntity.ok(ApiResponse.success(cobranzaService.listarTipoPagos(),"Listar tipo de pago", https));
    }
}
