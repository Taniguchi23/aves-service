package com.utpsistemas.distribuidoraavesservice.cobranza.controller;

import com.utpsistemas.distribuidoraavesservice.auth.helper.ApiResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaRequest;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.service.CobranzaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("aves-service/cobranza")
public class CobranzaController {
    @Autowired
    private CobranzaService cobranzaService;

    @PostMapping
    public ResponseEntity<ApiResponse<CobranzaResponse>> crearCobranza(@Valid @RequestBody CobranzaRequest cobranzaRequest, HttpServletRequest https) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(cobranzaService.crearCobranza(cobranzaRequest),"Cobranza creada", https));
    }
}
