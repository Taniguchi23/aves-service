package com.utpsistemas.distribuidoraavesservice.pedido.service;

import com.utpsistemas.distribuidoraavesservice.auth.entity.Usuario;
import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import com.utpsistemas.distribuidoraavesservice.auth.repository.UsuarioRepository;
import com.utpsistemas.distribuidoraavesservice.auth.security.CustomUserDetails;
import com.utpsistemas.distribuidoraavesservice.cliente.entity.Cliente;
import com.utpsistemas.distribuidoraavesservice.cliente.repository.ClienteRepository;
import com.utpsistemas.distribuidoraavesservice.cliente.repository.UsuarioClienteRepository;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.DetallePedidoRequest;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoRequest;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.DetallePedido;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import com.utpsistemas.distribuidoraavesservice.pedido.mapper.PedidoMapper;
import com.utpsistemas.distribuidoraavesservice.pedido.repository.PedidoRepository;
import com.utpsistemas.distribuidoraavesservice.tipoaves.entity.TipoAve;
import com.utpsistemas.distribuidoraavesservice.tipoaves.repository.TipoAveRepository;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    @Transactional
    @Override
    public PedidoResponse crearPedido(PedidoRequest request) {
        CustomUserDetails auth = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long usuarioId = auth.getId();

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ApiException("Cliente no encontrado con ID: " + request.clienteId(),HttpStatus.NOT_FOUND));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ApiException("Usuario no encontrado con ID: " + usuarioId,HttpStatus.CONFLICT));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUsuario(usuario);
        pedido.setObservaciones(request.observaciones());
        pedido.setEstado("Pendiente");
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
        return pedidoMapper.toResponse(guardado);
    }

    @Override
    public List<PedidoResponse> listarPedidosPorCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ApiException("Cliente no encontrado con ID: " + clienteId,HttpStatus.NOT_FOUND));

        CustomUserDetails auth = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long usuarioId = auth.getId();
        if (validarAsignacionCliente(usuarioId, clienteId)) {
            List<Pedido> pedidos = pedidoRepository.findByClienteIdAndEstadoIn(clienteId, List.of("Pendiente", "Pesado", "Entregado"));

            pedidos.sort(Comparator.comparingInt(p -> {
                return switch (p.getEstado()) {
                    case "Pendiente" -> 0;
                    case "Pesado"    -> 1;
                    case "Entregado" -> 2;
                    default          -> 3;
                };
            }));

            return pedidos.stream().map(pedidoMapper::toResponse).toList();
        }else {
            throw new ApiException("No tiene asignado a este cliente", HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @Override
    public PedidoResponse actualizarPedido(PedidoRequest pedidoRequest) {
        if (pedidoRequest.id() == null) {
            throw new ApiException("El ID del pedido es obligatorio para actualizar.", HttpStatus.BAD_REQUEST);
        }

        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long usuarioId = user.getId();

        Pedido pedido = pedidoRepository.findById(pedidoRequest.id())
                .orElseThrow(() -> new ApiException("Pedido no encontrado con ID: " + pedidoRequest.id(), HttpStatus.NOT_FOUND));

        if (!validarAsignacionCliente(usuarioId, pedidoRequest.clienteId())) {
            throw new ApiException("No tiene asignado este cliente", HttpStatus.NOT_FOUND);
        }

        if (pedido.getEstado().equals("Entregado") || pedido.getEstado().equals("Anulado")) {
            throw new ApiException("No se puede modificar un pedido entregado o anulado", HttpStatus.CONFLICT);
        }

        pedido.setObservaciones(pedidoRequest.observaciones());
        pedido.setEstado("Pendiente");

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
                existente.setMontoEstimado(peso.multiply(precio));
                existente.setEstado(1);
                nuevosDetalles.add(existente);
            } else {
                DetallePedido nuevo = new DetallePedido();
                nuevo.setPedido(pedido);
                nuevo.setTipoAve(tipoAve);
                nuevo.setCantidadPollo(d.cantidad());
                nuevo.setPeso(peso);
                nuevo.setPrecioXKilo(precio);
                nuevo.setMontoEstimado(peso.multiply(precio));
                nuevo.setEstado(1);
                nuevosDetalles.add(nuevo);
            }
        }

        for (DetallePedido actual : detallesActuales) {
            if (actual.getId() != null && !idsRecibidos.contains(actual.getId())) {
                actual.setEstado(0);
                nuevosDetalles.add(actual);
            }
        }

        pedido.setDetalles(nuevosDetalles);


        return pedidoMapper.toResponse(pedidoRepository.save(pedido));
    }

    @Override
    public boolean validarAsignacionCliente(Long usuarioId, Long clienteId){
        return usuarioClienteRepository.existsByUsuarioIdAndClienteIdAndEstado(usuarioId, clienteId, 'A');
    }
}
