package com.utpsistemas.distribuidoraavesservice.cobranza.service;

import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import com.utpsistemas.distribuidoraavesservice.auth.security.CustomUserDetails;
import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteMiniDTO;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.*;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Cobranza;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.FormaPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Pago;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.TipoPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.mapper.CobranzaMapper;
import com.utpsistemas.distribuidoraavesservice.cobranza.mapper.CobranzaPedidoMapper;
import com.utpsistemas.distribuidoraavesservice.cobranza.mapper.PagoMapper;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.CobranzaMovimientoRepository;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.CobranzaRepository;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.FormaPagoRepository;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.TipoPagoRepository;
import com.utpsistemas.distribuidoraavesservice.estado.dto.EstadoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.DetallePedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.PedidoDetalle;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import com.utpsistemas.distribuidoraavesservice.pedido.enums.EstadoPedidoEnum;
import com.utpsistemas.distribuidoraavesservice.pedido.mapper.DetallePedidoMapper;
import com.utpsistemas.distribuidoraavesservice.pedido.mapper.PedidoMapper;
import com.utpsistemas.distribuidoraavesservice.pedido.projection.PedidoDetalleProjection;
import com.utpsistemas.distribuidoraavesservice.pedido.repository.PedidoRepository;
import com.utpsistemas.distribuidoraavesservice.pedido.service.PedidoService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class CobranzaServiceImpl implements CobranzaService {

    @Autowired private CobranzaRepository cobranzaRepository;

    @Autowired private PedidoRepository pedidoRepository;

    @Autowired private CobranzaMapper  cobranzaMapper;

    @Autowired private PedidoService pedidoService;

    @Autowired private PagoService pagoService;

    @Autowired private PagoMapper pagoMapper;

    @Autowired private DetallePedidoMapper detallePedidoMapper;

    @Autowired private TipoPagoRepository tipoPagoRepository;

    @Autowired private FormaPagoRepository formaPagoRepository;

    @Autowired private CobranzaPedidoMapper cobranzaPedidoMapper;

    @Autowired private CobranzaMovimientoRepository cobranzaMovimientoRepository;

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
                .map(PedidoDetalle::getMontoEstimado)
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

        List<PedidoDetalle> detallesActivos = pedido.getDetalles().stream()
                .filter(det -> det.getEstado() == 1)
                .toList();

        BigDecimal nuevoMontoTotal = detallesActivos.stream()
                .map(PedidoDetalle::getMontoEstimado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cobranza.setMontoTotal(nuevoMontoTotal);

        List<Pago> pagosActivos = pagoService.obtenerPagosActivosPorCobranza(cobranza.getId());

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

        return new CobranzaRefreshResponse(
                cobranzaMapper.toResponse(cobranza),
                resumen
        );
    }

    @Override
    public List<CobranzaRefreshResponse> listarCobranzasAsignadas() {
        CustomUserDetails auth = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long usuarioId = auth.getId();

        List<Cobranza> cobranzas = cobranzaRepository.findAllWithPedidoAndCliente();

        List<CobranzaRefreshResponse> resultado = new ArrayList<>();

        for (Cobranza cobranza : cobranzas) {
            Pedido pedido = cobranza.getPedido();
            Long clienteId = pedido.getCliente().getId();

            if (!pedidoService.validarAsignacionCliente(usuarioId, clienteId)) continue;

            List<PedidoDetalle> detallesActivos = pedido.getDetalles().stream()
                    .filter(det -> det.getEstado() == 1)
                    .toList();

            BigDecimal nuevoMontoTotal = detallesActivos.stream()
                    .map(PedidoDetalle::getMontoEstimado)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<Pago> pagosActivos = pagoService.obtenerPagosActivosPorCobranza(cobranza.getId());

            BigDecimal totalPagos = BigDecimal.ZERO;
            BigDecimal totalDescuentos = BigDecimal.ZERO;
            BigDecimal totalExtras = BigDecimal.ZERO;
            List<PagoResponse> listaPagos = new ArrayList<>();

            for (Pago pago : pagosActivos) {
                String tipo = pago.getTipoPago().getNombre().toUpperCase();
                listaPagos.add(pagoMapper.toResponse(pago));

                switch (tipo) {
                    case "PAGO" -> totalPagos = totalPagos.add(pago.getMonto());
                    case "DESCUENTO" -> totalDescuentos = totalDescuentos.add(pago.getMonto());
                    case "EXTRA" -> totalExtras = totalExtras.add(pago.getMonto());
                }
            }

            BigDecimal totalPagosYDescuentos = totalPagos.add(totalDescuentos);
            BigDecimal totalConExtras = nuevoMontoTotal.add(totalExtras);
            BigDecimal restante = totalConExtras.subtract(totalPagosYDescuentos);

            String estado;
            if (totalPagosYDescuentos.compareTo(BigDecimal.ZERO) == 0) {
                estado = "Pendiente";
            } else if (restante.compareTo(BigDecimal.ZERO) == 0) {
                estado = "Pagado";
            } else {
                estado = "Parcial";
            }

            cobranza.setEstado(estado);
            cobranza.setMontoTotal(nuevoMontoTotal);

            CobranzaResponse cobranzaResponse = cobranzaMapper.toResponse(cobranza);

            PagoResumenResponse resumen = new PagoResumenResponse(
                    totalPagosYDescuentos,
                    restante,
                    totalPagos,
                    totalDescuentos,
                    totalExtras,
                    listaPagos
            );

            resultado.add(new CobranzaRefreshResponse(cobranzaResponse, resumen));
        }

        return resultado;
    }

    @Override
    public List<FormaPago> listarFormaPagos() {
        return formaPagoRepository.findAll();
    }

    @Override
    public List<TipoPago> listarTipoPagos() {
        return tipoPagoRepository.findAll();
    }


    @Override
    public List<CobranzaClienteResumenResponse> listarResumenCobranzasPorUsuario(Long usuarioId) {

        /*CustomUserDetails auth = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long usrId = auth.getId();
        if (usrId != usuarioId) {
            throw new ApiException("El usuario ingresado no corresponde", HttpStatus.CONFLICT);
        }*/

        var estadosCobranza = List.of(
                EstadoPedidoEnum.EN_COBRANZA.getId(),
                EstadoPedidoEnum.PARCIAL.getId()
        );

        var filas = pedidoRepository.resumirPorUsuarioYEstados(usuarioId, estadosCobranza);

        return filas.stream()
                .map(r -> {

                    return new CobranzaClienteResumenResponse(
                            new ClienteMiniDTO(r.getClienteId(), r.getClienteNombre()),
                            r.getTotalImporte(),
                            r.getCantidadPedidos() == null ? 0 : r.getCantidadPedidos().intValue(),
                            new EstadoResponse(r.getEstadoId(), r.getEstadoNombre()),
                            r.getFechaMax(),
                            r.getTotalPagado(),
                            r.getTotalDescuento(),
                            r.getTotalSaldo()
                    );
                })
                .toList();
    }

    @Override
    public List<CobranzaPedidoResponse> listarCobranzaPorUsuario(Long usuarioId) {
        var estados = List.of(EstadoPedidoEnum.EN_COBRANZA.getId(), EstadoPedidoEnum.PARCIAL.getId());

        var pedidos = pedidoRepository.fetchPedidosConDetallesPorUsuario(usuarioId, estados);
        if (pedidos.isEmpty()) return List.of();

        return pedidos.stream()
                .map(cobranzaPedidoMapper::toResponse)
                .toList();
    }
    @Override
    public List<CobranzaPedidoResponse> listarCobranzaPorUsuarioAndCliente(Long usuarioId, Long clienteId) {
        var estados = List.of(EstadoPedidoEnum.EN_COBRANZA.getId(), EstadoPedidoEnum.PARCIAL.getId());

        var pedidos = pedidoRepository.fetchPedidosConDetallesPorUsuarioAndCliente(usuarioId, clienteId, estados);
        if (pedidos.isEmpty()) return List.of();

        return pedidos.stream()
                .map(cobranzaPedidoMapper::toResponse)
                .toList();
    }

    @Override
    public CobranzaPedidoResponse cobranzaPedidoPorId(Long usuarioId, Long pedidoId) {
        var estados = List.of(
                EstadoPedidoEnum.EN_COBRANZA.getId(),
                EstadoPedidoEnum.PARCIAL.getId()
        );

        var pedido = pedidoRepository
                .findPedidoConDetallesPorIdYUsuario(pedidoId, usuarioId, estados)
                .orElseThrow(() -> new ApiException("Pedido no encontrado o no autorizado", HttpStatus.NOT_FOUND));


        var movimientos = cobranzaMovimientoRepository.findByPedidoIds(List.of(pedidoId));

        return cobranzaPedidoMapper.toResponse(pedido, movimientos);
    }



}
