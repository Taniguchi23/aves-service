package com.utpsistemas.distribuidoraavesservice.pedido.service;

import com.utpsistemas.distribuidoraavesservice.auth.entity.Usuario;
import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import com.utpsistemas.distribuidoraavesservice.auth.repository.UsuarioRepository;
import com.utpsistemas.distribuidoraavesservice.auth.security.CustomUserDetails;
import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteMiniDTO;
import com.utpsistemas.distribuidoraavesservice.cliente.entity.Cliente;
import com.utpsistemas.distribuidoraavesservice.cliente.repository.ClienteRepository;
import com.utpsistemas.distribuidoraavesservice.cliente.repository.UsuarioClienteRepository;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaDTO;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaResumenDTO;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Cobranza;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.CobranzaRepository;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.PagoRepository;
import com.utpsistemas.distribuidoraavesservice.estado.dto.EstadoDTO;
import com.utpsistemas.distribuidoraavesservice.estado.dto.EstadoResponse;
import com.utpsistemas.distribuidoraavesservice.estado.entity.Estado;
import com.utpsistemas.distribuidoraavesservice.estado.repository.EstadoRepository;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.*;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.DetallePedido;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import com.utpsistemas.distribuidoraavesservice.pedido.mapper.DetallePedidoMapper;
import com.utpsistemas.distribuidoraavesservice.pedido.mapper.PedidoMapper;
import com.utpsistemas.distribuidoraavesservice.pedido.repository.PedidoRepository;
import com.utpsistemas.distribuidoraavesservice.tipoaves.entity.TipoAve;
import com.utpsistemas.distribuidoraavesservice.tipoaves.repository.TipoAveRepository;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioMiniDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoAveRepository tipoAveRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PedidoMapper pedidoMapper;

    @Autowired
    private UsuarioClienteRepository usuarioClienteRepository;

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private CobranzaRepository cobranzaRepository;

    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private DetallePedidoMapper detallePedidoMapper;

    @Transactional
    @Override
    public PedidoResponse crearPedido(PedidoRequest request) {
        CustomUserDetails auth = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long usuarioId = auth.getId();

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ApiException("Cliente no encontrado con ID: " + request.clienteId(),HttpStatus.NOT_FOUND));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ApiException("Usuario no encontrado con ID: " + usuarioId,HttpStatus.CONFLICT));


        Estado estado = estadoRepository.findById(1L)
                .orElseThrow(() -> new ApiException("Estado no encontrado", HttpStatus.NOT_FOUND));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUsuario(usuario);
        pedido.setObservaciones(request.observaciones());
        pedido.setEstado(estado);
        pedido.setFechaCreacion(LocalDateTime.now());

        List<DetallePedido> detalles = new ArrayList<>();

        for (DetallePedidoRequest d : request.detallePedido()) {
            TipoAve tipoAve = tipoAveRepository.findById(d.tipoAveId())
                    .orElseThrow(() -> new ApiException("Tipo de ave no encontrado con ID: " + d.tipoAveId(), HttpStatus.NOT_FOUND));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setTipoAve(tipoAve);
            detalle.setCantidadPollo(d.cantidad());
            detalle.setPeso(d.peso() != null ? d.peso() : BigDecimal.ZERO);
            detalle.setPrecioXKilo(d.precioXKilo()  != null ? d.precioXKilo() : BigDecimal.ZERO);

            BigDecimal monto = BigDecimal.ZERO;
            if (d.peso() != null && d.precioXKilo() != null) {
                monto = d.peso().multiply(d.precioXKilo());
            }
            detalle.setMontoEstimado(monto);
            detalle.setEstado(1);

            detalles.add(detalle);
        }

        pedido.setDetalles(detalles);

        Pedido guardado = pedidoRepository.save(pedido);
        return pedidoMapper.toResponse(guardado,null);
    }

    @Transactional()
    public List<PedidoResponse> listarPedidosAsignadosAlUsuario() {
        CustomUserDetails auth = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long usuarioId = auth.getId();

        List<Long> clienteIds = usuarioClienteRepository.findClienteIdsActivosByUsuario(usuarioId);
        if (clienteIds.isEmpty()) return List.of();

        List<Pedido> pedidos = pedidoRepository.findByCliente_IdIn(clienteIds);
        if (pedidos.isEmpty()) return List.of();

        // traemos cobranzas + pagos (como ya hacías)
        List<Long> pedidoIds = pedidos.stream().map(Pedido::getId).toList();
        List<Cobranza> cobranzas = cobranzaRepository.findByPedidoIds(pedidoIds);
        Map<Long, Cobranza> cobranzaByPedidoId = cobranzas.stream()
                .collect(Collectors.toMap(c -> c.getPedido().getId(), Function.identity()));

        List<Long> cobranzaIds = cobranzas.stream().map(Cobranza::getId).toList();
        Map<Long, BigDecimal> sumPagosByCobranza = new HashMap<>();
        if (!cobranzaIds.isEmpty()) {
            for (Object[] row : pagoRepository.sumMontosActivosByCobranzaIds(cobranzaIds)) {
                Long cId = (Long) row[0];
                BigDecimal sum = (BigDecimal) row[1];
                sumPagosByCobranza.put(cId, sum != null ? sum : BigDecimal.ZERO);
            }
        }

        // mapear pedidos
        List<PedidoResponse> responses = pedidos.stream().map(p -> {
            CobranzaDTO cobranzaDTO = null;
            Cobranza c = cobranzaByPedidoId.get(p.getId());
            if (c != null) {
                BigDecimal pagado = sumPagosByCobranza.getOrDefault(c.getId(), BigDecimal.ZERO);
                BigDecimal total = c.getMontoTotal() != null ? c.getMontoTotal() : BigDecimal.ZERO;
                BigDecimal saldo = total.subtract(pagado);

                cobranzaDTO = new CobranzaDTO(
                        c.getId(),
                        c.getEstado(),
                        total,
                        pagado,
                        saldo
                );
            }

            return pedidoMapper.toResponse(p, cobranzaDTO);
        }).toList();

        // ordenar como ya lo tenías
        Map<String, Integer> ordenEstados = Map.of(
                "REGISTRADO", 0,
                "PENDIENTE", 1,
                "EN_COBRANZA", 2,
                "PARCIAL", 3,
                "COMPLETADO", 4,
                "ANULADO", 5
        );

        Comparator<PedidoResponse> cmp = Comparator
                .comparingInt((PedidoResponse x) ->
                        ordenEstados.getOrDefault(
                                (x.estado() != null && x.estado().nombre() != null)
                                        ? x.estado().nombre().toUpperCase()
                                        : "",
                                Integer.MAX_VALUE))
                .thenComparing(PedidoResponse::fechaCreacion,
                        Comparator.nullsLast(Comparator.reverseOrder()));

        return responses.stream().sorted(cmp).toList();
    }



    @Transactional
    @Override
    public PedidoResponse actualizarPedido(PedidoRequest pedidoRequest) {
        if (pedidoRequest.id() == null) {
            throw new ApiException("El ID del pedido es obligatorio para actualizar.", HttpStatus.BAD_REQUEST);
        }

        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long usuarioId = user.getId();

        Pedido pedido = pedidoRepository.findById(pedidoRequest.id())
                .orElseThrow(() -> new ApiException("Pedido no encontrado con ID: " + pedidoRequest.id(), HttpStatus.NOT_FOUND));

        if (!validarAsignacionCliente(usuarioId, pedidoRequest.clienteId())) {
            throw new ApiException("No tiene asignado este cliente", HttpStatus.NOT_FOUND);
        }

        if ("Completado".equalsIgnoreCase(pedido.getEstado().getNombre()) || "Anulado".equalsIgnoreCase(pedido.getEstado().getNombre())) {
            throw new ApiException("No se puede modificar un pedido completado o anulado", HttpStatus.CONFLICT);
        }

        pedido.setObservaciones(pedidoRequest.observaciones());

        List<DetallePedido> detallesActuales = pedido.getDetalles(); // existentes
        List<DetallePedido> nuevosDetalles = new ArrayList<>();

        List<Long> idsRecibidos = pedidoRequest.detallePedido().stream()
                .map(DetallePedidoRequest::id)
                .filter(Objects::nonNull)
                .toList();

        for (DetallePedidoRequest d : pedidoRequest.detallePedido()) {
            TipoAve tipoAve = tipoAveRepository.findById(d.tipoAveId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de ave no encontrado con ID: " + d.tipoAveId()));

            BigDecimal peso = d.peso() != null ? d.peso() : BigDecimal.ZERO;
            BigDecimal precio = d.precioXKilo() != null ? d.precioXKilo() : BigDecimal.ZERO;

            if (d.id() != null) {
                DetallePedido existente = detallesActuales.stream()
                        .filter(dp -> dp.getId().equals(d.id()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("Detalle no encontrado con ID: " + d.id()));

                existente.setTipoAve(tipoAve);
                existente.setCantidadPollo(d.cantidad());
                existente.setPeso(peso);
                existente.setPrecioXKilo(precio);

                // Recalcular montoEstimado SIEMPRE a partir de peso * precio
                BigDecimal montoEstimado = (peso != null && precio != null)
                        ? peso.multiply(precio)
                        : BigDecimal.ZERO;
                existente.setMontoEstimado(montoEstimado);

                existente.setEstado(1);
                nuevosDetalles.add(existente);
            } else {
                DetallePedido nuevo = new DetallePedido();
                nuevo.setPedido(pedido);
                nuevo.setTipoAve(tipoAve);
                nuevo.setCantidadPollo(d.cantidad());
                nuevo.setPeso(peso);
                nuevo.setPrecioXKilo(precio);

                BigDecimal montoEstimado = (peso != null && precio != null)
                        ? peso.multiply(precio)
                        : BigDecimal.ZERO;
                nuevo.setMontoEstimado(montoEstimado);

                nuevo.setEstado(1);
                nuevosDetalles.add(nuevo);
            }
        }

        // Marcar como inactivos los que no vinieron
        for (DetallePedido actual : detallesActuales) {
            if (actual.getId() != null && !idsRecibidos.contains(actual.getId())) {
                actual.setEstado(0);
                nuevosDetalles.add(actual);
            }
        }

        pedido.setDetalles(nuevosDetalles);

        // === Reglas de negocio post-procesamiento ===

        // Suma del monto estimado SOLO de detalles activos
//        BigDecimal totalEstimado = nuevosDetalles.stream()
//                .filter(dp -> dp.getEstado() != null && dp.getEstado() == 1)
//                .map(DetallePedido::getMontoEstimado)
//                .filter(Objects::nonNull)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);

//        pedido.(totalEstimado);

        // ¿Todos los detalles activos tienen peso y precio > 0?
        boolean todosCompletos = nuevosDetalles.stream()
                .filter(dp -> dp.getEstado() != null && dp.getEstado() == 1)
                .allMatch(dp ->
                        dp.getPeso() != null && dp.getPeso().compareTo(BigDecimal.ZERO) > 0 &&
                                dp.getPrecioXKilo() != null && dp.getPrecioXKilo().compareTo(BigDecimal.ZERO) > 0
                );

        // Si todos completos y el pedido está en "Pendiente", cambiar a "Registrado"
        if (todosCompletos && "Pendiente".equalsIgnoreCase(pedido.getEstado().getNombre())) {
            Estado registrado = estadoRepository.findByNombreIgnoreCase("Registrado")
                    .orElseThrow(() -> new ApiException("Estado 'Registrado' no configurado", HttpStatus.CONFLICT));
            pedido.setEstado(registrado);
        }

        Pedido guardado = pedidoRepository.save(pedido);

        CobranzaDTO cobranzaDTO = null;
        Optional<Cobranza> cobranzaOpt = cobranzaRepository.findByPedidoId(guardado.getId());
        if (cobranzaOpt.isPresent()) {
            Cobranza c = cobranzaOpt.get();

            // Sumar pagos activos
            BigDecimal pagado = pagoRepository.sumMontosActivosByCobranzaId(c.getId());
            if (pagado == null) pagado = BigDecimal.ZERO;

            BigDecimal total = c.getMontoTotal() != null ? c.getMontoTotal() : BigDecimal.ZERO;
            BigDecimal saldo = total.subtract(pagado);

            cobranzaDTO = new CobranzaDTO(
                    c.getId(),
                    c.getEstado(),
                    total,
                    pagado,
                    saldo
            );
        }
        return pedidoMapper.toResponse(guardado, cobranzaDTO);

    }

    @Override
    public boolean validarAsignacionCliente(Long usuarioId, Long clienteId){
        return usuarioClienteRepository.existsByUsuarioIdAndClienteIdAndEstado(usuarioId, clienteId, 'A');
    }
}
