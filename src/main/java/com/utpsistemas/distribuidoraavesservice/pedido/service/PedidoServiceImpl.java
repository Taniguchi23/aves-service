package com.utpsistemas.distribuidoraavesservice.pedido.service;

import com.utpsistemas.distribuidoraavesservice.auth.entity.Usuario;
import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import com.utpsistemas.distribuidoraavesservice.auth.repository.UsuarioRepository;
import com.utpsistemas.distribuidoraavesservice.cliente.entity.Cliente;
import com.utpsistemas.distribuidoraavesservice.cliente.repository.ClienteRepository;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoRequest;
import com.utpsistemas.distribuidoraavesservice.pedido.dto.PedidoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PedidoServiceImpl implements PedidoService {
    @Autowired
    private ClienteRepository clienteRepository;


    @Override
    public PedidoResponse crearPedido(PedidoRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ApiException("Cliente no encontrado con ID: " + request.clienteId(), HttpStatus.NOT_FOUND));

        // 2. Validar usuario
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + request.usuarioId()));

        // 3. Crear pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUsuario(usuario);
        pedido.setObservaciones(request.observaciones());
        pedido.setEstado("Pendiente");
        pedido.setFechaCreacion(LocalDate.now());
        pedido.setHoraCreacion(LocalTime.now());

        List<DetallePedido> detalles = new ArrayList<>();

        for (DetallePedidoRequest d : request.detalles()) {
            TipoAve tipoAve = tipoAveRepository.findById(d.tipoAveId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de ave no encontrado con ID: " + d.tipoAveId()));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setTipoAve(tipoAve);
            detalle.setCantidadPollo(d.cantidad());
            detalle.setPeso(d.peso());
            detalle.setPrecioXKilo(d.precioXKilo());

            if (d.peso() != null && d.precioXKilo() != null) {
                detalle.setMontoEstimado(d.peso().multiply(d.precioXKilo()));
            } else {
                detalle.setMontoEstimado(BigDecimal.ZERO);
            }

            detalle.setEstado(1);
            detalles.add(detalle);
        }

        pedido.setDetalles(detalles);

        Pedido guardado = pedidoRepository.save(pedido);
        return pedidoMapper.toResponse(guardado);
    }

}
