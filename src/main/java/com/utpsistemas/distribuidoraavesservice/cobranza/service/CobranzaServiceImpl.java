package com.utpsistemas.distribuidoraavesservice.cobranza.service;

import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import com.utpsistemas.distribuidoraavesservice.auth.security.CustomUserDetails;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaRequest;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.CobranzaResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Cobranza;
import com.utpsistemas.distribuidoraavesservice.cobranza.mapper.CobranzaMapper;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.CobranzaRepository;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.DetallePedido;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import com.utpsistemas.distribuidoraavesservice.pedido.repository.PedidoRepository;
import com.utpsistemas.distribuidoraavesservice.pedido.service.PedidoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

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
}
