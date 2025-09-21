package com.utpsistemas.distribuidoraavesservice.cobranza.controller;

import com.utpsistemas.distribuidoraavesservice.auth.helper.ApiResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.*;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.FormaPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.TipoPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.service.CobranzaMovimientoService;
import com.utpsistemas.distribuidoraavesservice.cobranza.service.CobranzaService;
import com.utpsistemas.distribuidoraavesservice.cobranza.service.PagoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("aves-service/cobranza/movimientos")
@AllArgsConstructor
    public class CobranzaMovimientoController {

    private final CobranzaMovimientoService cobranzaMovimientoService;

    @PostMapping("/masivo")
    public ResponseEntity<ApiResponse<MovimientoMasivoResponse>> crearMasivo(
            @Valid @RequestBody MovimientoMasivoRequest request,
            HttpServletRequest http) {

        var resp = cobranzaMovimientoService.crearMovimientosMasivos(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(resp, "Movimientos creados", http));
    }

}
