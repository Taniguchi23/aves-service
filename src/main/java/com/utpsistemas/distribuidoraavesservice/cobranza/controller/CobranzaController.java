package com.utpsistemas.distribuidoraavesservice.cobranza.controller;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.utpsistemas.distribuidoraavesservice.auth.helper.ApiResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.*;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.FormaPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.TipoPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.service.CobranzaService;
import com.utpsistemas.distribuidoraavesservice.cobranza.service.PagoService;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("aves-service/cobranza")
@AllArgsConstructor
    public class CobranzaController {

    private final CobranzaService cobranzaService;

    private final PagoService pagoService;

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

    //---------------------------------------

    //@PreAuthorize("hasRole('Cobrador') or #usuarioId == authentication.principal.id")
    @PreAuthorize("hasRole('Cobrador')")
    @GetMapping("resumen/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<List<CobranzaClienteResumenResponse>>> listarResumenPorUsuario(
            @PathVariable Long usuarioId, HttpServletRequest https) {

        var data = cobranzaService.listarResumenCobranzasPorUsuario(usuarioId);
        return ResponseEntity.ok(ApiResponse.success(data, "Resumen de cobranzas", https));
    }

    //@PreAuthorize("hasRole('Cobrador')")
    @GetMapping("pedidos/usuario/{usuarioId}/pedido/{pedidoId}")
    public ResponseEntity<ApiResponse<CobranzaPedidoResponse>> cobranzaPedidoPorId(
            @PathVariable Long usuarioId,
            @PathVariable Long pedidoId,
            HttpServletRequest https) {

        var data = cobranzaService.cobranzaPedidoPorId(usuarioId, pedidoId);
        return ResponseEntity.ok(ApiResponse.success(data, "Resumen de cobranzas", https));
    }

    //@PreAuthorize("hasRole('Cobrador')")
    @GetMapping("pedidos/usuario/{usuarioId}/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<CobranzaPedidoResponse>>> listarCobranzaPorUsuario(
            @PathVariable Long usuarioId,@PathVariable Long clienteId, HttpServletRequest https) {

        var data = cobranzaService.listarCobranzaPorUsuarioAndCliente(usuarioId, clienteId);
        return ResponseEntity.ok(ApiResponse.success(data, "Resumen de cobranzas", https));
    }

    private final SpringTemplateEngine templateEngine;

    @GetMapping(value = "impresion/usuario/{usuarioId}/cliente/{clienteId}/recibos.pdf", produces = "application/pdf")
    public void descargarRecibosPdf(@PathVariable Long usuarioId,
                                    @PathVariable Long clienteId,
                                    HttpServletResponse response) throws Exception {

        var pedidos = cobranzaService.listarCobranzaPorUsuarioAndCliente(usuarioId, clienteId);

        // ---- Totales globales (null-safe) ----
        java.math.BigDecimal ZERO = java.math.BigDecimal.ZERO;

        java.util.function.Function<java.math.BigDecimal, java.math.BigDecimal> nz = v -> v != null ? v : ZERO;

        java.math.BigDecimal totalImporteAll = pedidos.stream()
                .map(p -> nz.apply(p.totalImporte()))
                .reduce(ZERO, java.math.BigDecimal::add);

        java.math.BigDecimal totalACuentaAll = pedidos.stream()
                .map(p -> nz.apply(p.totalPagado()).add(nz.apply(p.totalDescuento())))
                .reduce(ZERO, java.math.BigDecimal::add);

        java.math.BigDecimal totalSaldoAll = pedidos.stream()
                .map(p -> nz.apply(p.totalSaldo()))
                .reduce(ZERO, java.math.BigDecimal::add);

        var ctx = new org.thymeleaf.context.Context(new java.util.Locale("es","PE"));
        ctx.setVariable("usuarioId", usuarioId);
        ctx.setVariable("pedidos", pedidos);
        ctx.setVariable("avicolaNombre", "NOMBRE DE LA AVÍCOLA");
        ctx.setVariable("whatsapp", "999999999");
        // ctx.setVariable("logoPath", "/images/logo.png");

        // Totales globales para el pie
        ctx.setVariable("totalImporteAll", totalImporteAll);
        ctx.setVariable("totalACuentaAll", totalACuentaAll);
        ctx.setVariable("totalSaldoAll", totalSaldoAll);
        ctx.setVariable("hayPedidos", !pedidos.isEmpty());

        String html = templateEngine.process("recibos-ticket", ctx);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"recibos-" + usuarioId + ".pdf\"");

        String baseUrl = new org.springframework.core.io.ClassPathResource("static/")
                .getURL().toExternalForm();

        try (var os = response.getOutputStream()) {
            var builder = new com.openhtmltopdf.pdfboxout.PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, baseUrl);
            // Tamaño: 68mm x 220mm (ajusta el alto si se corta)
            builder.useDefaultPageSize(68, 220, com.openhtmltopdf.pdfboxout.PdfRendererBuilder.PageSizeUnits.MM);
            builder.toStream(os);
            builder.run();
        }
    }

}
