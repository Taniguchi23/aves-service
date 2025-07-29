package com.utpsistemas.distribuidoraavesservice.cobranza.service;

import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import com.utpsistemas.distribuidoraavesservice.auth.security.CustomUserDetails;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.*;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Cobranza;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Pago;
import com.utpsistemas.distribuidoraavesservice.cobranza.mapper.CobranzaMapper;
import com.utpsistemas.distribuidoraavesservice.cobranza.mapper.PagoMapper;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.CobranzaRepository;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.PagoRepository;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.DetallePedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.DetallePedido;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import com.utpsistemas.distribuidoraavesservice.pedido.mapper.DetallePedidoMapper;
import com.utpsistemas.distribuidoraavesservice.pedido.repository.PedidoRepository;
import com.utpsistemas.distribuidoraavesservice.pedido.service.PedidoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CobranzaServiceImpl implements CobranzaService {

    @Autowired
    private CobranzaRepository cobranzaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CobranzaMapper  cobranzaMapper;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PagoMapper pagoMapper;

    @Autowired
    private DetallePedidoMapper detallePedidoMapper;

    @Override
    public CobranzaResponse crearCobranza(CobranzaRequest request) {
        CustomUserDetails auth = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long usuarioId = auth.getId();
        Pedido pedido = pedidoRepository.findById(request.pedidoId())
                .orElseThrow(() -> new ApiException("Pedido no encontrado con ID: " + request.pedidoId(), HttpStatus.NOT_FOUND));

        Long clienteId = pedido.getCliente().getId();
        if (!pedidoService.validarAsignacionCliente(usuarioId, clienteId))
            throw new ApiException("No tiene asignado este cliente", HttpStatus.CONFLICT);

        if (cobranzaRepository.existsByPedidoId(pedido.getId())) {
            throw new ApiException("Ya existe una cobranza para este pedido", HttpStatus.CONFLICT);
        }

        BigDecimal total = pedido.getDetalles().stream()
                .filter(det -> det.getEstado() == 1)
                .map(DetallePedido::getMontoEstimado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Cobranza cobranza = new Cobranza();
        cobranza.setPedido(pedido);
        cobranza.setMontoTotal(total);
        cobranza.setFecha(LocalDate.now());
        cobranza.setEstado("Pendiente");

        return cobranzaMapper.toResponse(cobranzaRepository.save(cobranza));
    }

    @Override
    public CobranzaRefreshResponse refrescarCobranza(CobranzaRefreshRequest request) {
        CustomUserDetails auth = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long usuarioId = auth.getId();

        Cobranza cobranza = cobranzaRepository.findById(request.cobranzaId())
                .orElseThrow(() -> new ApiException("Cobranza no encontrada", HttpStatus.NOT_FOUND));

        Pedido pedido = pedidoRepository.findById(cobranza.getPedido().getId())
                .orElseThrow(() -> new ApiException("Pedido no encontrado" , HttpStatus.NOT_FOUND));

        if (!pedidoService.validarAsignacionCliente(usuarioId, pedido.getCliente().getId())) {
            throw new ApiException("No tiene asignado este cliente", HttpStatus.CONFLICT);
        }

        // 1. Calcular monto total desde detalles activos
        List<DetallePedido> detallesActivos = pedido.getDetalles().stream()
                .filter(det -> det.getEstado() == 1)
                .toList();

        BigDecimal nuevoMontoTotal = detallesActivos.stream()
                .map(DetallePedido::getMontoEstimado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cobranza.setMontoTotal(nuevoMontoTotal);

        // 2. Obtener pagos activos
        List<Pago> pagosActivos = pagoService.obtenerPagosActivosPorCobranza(cobranza.getId());

        // 3. Agrupar y sumar pagos por tipo
        List<PagoResponse> listaPagos = new ArrayList<>();

        BigDecimal totalPagos = BigDecimal.ZERO;
        BigDecimal totalDescuentos = BigDecimal.ZERO;
        BigDecimal totalExtras = BigDecimal.ZERO;

        for (Pago pago : pagosActivos) {
            String tipo = pago.getTipoPago().getNombre().toUpperCase();
            PagoResponse pagoResponse = pagoMapper.toResponse(pago);
            listaPagos.add(pagoResponse);

            switch (tipo) {
                case "PAGO" -> totalPagos = totalPagos.add(pago.getMonto());
                case "DESCUENTO" -> totalDescuentos = totalDescuentos.add(pago.getMonto());
                case "EXTRA" -> totalExtras = totalExtras.add(pago.getMonto());
            }
        }

        // 4. Calcular total pagado y restante
        BigDecimal totalPagosYDescuentos = totalPagos.add(totalDescuentos);
        BigDecimal totalConExtras = nuevoMontoTotal.add(totalExtras);
        BigDecimal restante = totalConExtras.subtract(totalPagosYDescuentos);

        if (totalPagosYDescuentos.compareTo(BigDecimal.ZERO) == 0) {
            cobranza.setEstado("Pendiente");
        } else if (restante.compareTo(BigDecimal.ZERO) == 0) {
            cobranza.setEstado("Pagado");
        } else {
            cobranza.setEstado("Parcial");
        }

        cobranza = cobranzaRepository.save(cobranza);

        // 6. Preparar respuesta enriquecida
        List<DetallePedidoResponse> detalleResponses = detallesActivos.stream()
                .map(detallePedidoMapper::toResponse)
                .toList();

        CobranzaResponse response = cobranzaMapper.toResponse(cobranza);

        PagoResumenResponse resumen = new PagoResumenResponse(
                totalPagosYDescuentos,
                restante,
                totalPagos,
                totalDescuentos,
                totalExtras,
                listaPagos
        );

        // Recomponer la respuesta final (si CobranzaResponse es record)
        return new CobranzaRefreshResponse(
                cobranzaMapper.toResponse(cobranza),
                resumen
        );
    }

}
