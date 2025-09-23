package com.utpsistemas.distribuidoraavesservice.cobranza.controller;


import com.utpsistemas.distribuidoraavesservice.auth.helper.ApiResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.*;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.PedidoCobranzaMovimientoCategoria;
import com.utpsistemas.distribuidoraavesservice.cobranza.enums.TipoPedidoCobranzaMovimiento;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.CobranzaMovimientoCategoriaRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("aves-service/movimiento")
@AllArgsConstructor
    public class MovimientoCategoriaController {

    private final CobranzaMovimientoCategoriaRepository repository;

    @GetMapping("/categoria/descuento")
    public ResponseEntity<ApiResponse<List<PedidoCobranzaMovimientoCategoria>>> listarCategoriasDescuento(HttpServletRequest http) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(repository.findByTipo(TipoPedidoCobranzaMovimiento.DESCUENTO),
                        "categorias: DESCUENTO", http));
    }

    @GetMapping("/categoria/pago")
    public ResponseEntity<ApiResponse<List<PedidoCobranzaMovimientoCategoria>>> listarCategoriasPago(HttpServletRequest http) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(repository.findByTipo(TipoPedidoCobranzaMovimiento.PAGO),
                        "categorias: PAGO", http));
    }
}
