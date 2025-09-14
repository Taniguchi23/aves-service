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
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class PedidoServiceImpl implements PedidoService {


    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoAveRepository tipoAveRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final UsuarioClienteRepository usuarioClienteRepository;
    private final EstadoRepository estadoRepository;
    private final CobranzaRepository cobranzaRepository;
    private final PagoRepository pagoRepository;
    private final DetallePedidoMapper detallePedidoMapper;

    @Transactional
    @Override
    public PedidoResponse crearPedido(PedidoRequest request) {
        // 1. Obtener el usuario autenticado desde el contexto de seguridad de Spring.
        CustomUserDetails auth = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long usuarioId = auth.getId();

        // 2. Buscar el cliente en la base de datos usando el ID proporcionado en la solicitud.
        // Si no se encuentra, se lanza una excepción ApiException con estado 404 Not Found.
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ApiException("Cliente no encontrado con ID: " + request.clienteId(),HttpStatus.NOT_FOUND));

        // 3. Buscar el usuario que está creando el pedido.
        // Si por alguna razón no existe en la BD (poco probable), se lanza una excepción.
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ApiException("Usuario no encontrado con ID: " + usuarioId,HttpStatus.CONFLICT));




        // 5. Crear la entidad principal del Pedido y establecer sus propiedades.
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUsuario(usuario);
        pedido.setObservaciones(request.observaciones());
        pedido.setFechaCreacion(LocalDateTime.now()); // Se establece la fecha y hora actual.

        boolean todosCompletos = true;

        // 6. Procesar la lista de detalles del pedido que vienen en la solicitud.
        List<DetallePedido> detalles = new ArrayList<>();
        for (DetallePedidoRequest d : request.detallePedido()) {
            TipoAve tipoAve = tipoAveRepository.findById(d.tipoAveId())
                    .orElseThrow(() -> new ApiException("Tipo de ave no encontrado con ID: " + d.tipoAveId(), HttpStatus.NOT_FOUND));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setTipoAve(tipoAve);
            detalle.setCantidadPollo(d.cantidad());
            detalle.setMermaKg(d.mermaKg());
            detalle.setOpDirecta(d.opDirecta());
            detalle.setPeso(d.peso() != null ? d.peso() : BigDecimal.ZERO);
            detalle.setPrecioXKilo(d.precioXKilo()  != null ? d.precioXKilo() : BigDecimal.ZERO);
            detalle.setTipoMerma(d.tipoMerma());

            // Calcular monto
            BigDecimal monto = BigDecimal.ZERO;
            if (d.peso() != null && d.precioXKilo() != null) {
                monto = d.peso().multiply(d.precioXKilo());
            }
            detalle.setMontoEstimado(monto);

            // Estado por defecto del detalle
            detalle.setEstado(1);

            // Validar si el detalle está completo
            boolean detalleCompleto = d.cantidad() != null
                    && d.mermaKg() != null
                    && d.opDirecta() != null
                    && d.peso() != null
                    && d.precioXKilo() != null
                    && d.tipoMerma() != null;

            if (!detalleCompleto) {
                todosCompletos = false;
            }

            detalles.add(detalle);
        }
        // 7. Asociar la lista completa de detalles al pedido.
        pedido.setDetalles(detalles);

        // guardar cantidad y precio total
        List<DetallePedido> activos = detalles.stream()
                .filter(dp -> dp.getEstado() != null && dp.getEstado() == 1)
                .toList();

        BigDecimal importeTotal = activos.stream()
                .map(dp -> {
                    BigDecimal peso     = dp.getPeso() != null ? dp.getPeso() : BigDecimal.ZERO;
                    BigDecimal precio   = dp.getPrecioXKilo() != null ? dp.getPrecioXKilo() : BigDecimal.ZERO;
                    BigDecimal merma    = dp.getMermaKg() != null ? dp.getMermaKg() : BigDecimal.ZERO;
                    int cantidad        = dp.getCantidadPollo() != null ? dp.getCantidadPollo() : 0;

                    if (Boolean.TRUE.equals(dp.getOpDirecta())) {
                        return peso.multiply(precio)
                                .add(merma.multiply(BigDecimal.valueOf(cantidad)).multiply(precio));
                    } else {
                        return peso.multiply(precio);
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setImporteTotal(importeTotal);
        pedido.setCantidadDetalles(activos.size());


        Estado estadoPedido = estadoRepository.findById(todosCompletos ? 2L : 1L)
                .orElseThrow(() -> new ApiException("Estado no encontrado", HttpStatus.NOT_FOUND));
        pedido.setEstado(estadoPedido);

        // 8. Guardar el pedido en la base de datos. Gracias a la relación en cascada,
        // los detalles también se guardarán automáticamente.
        Pedido guardado = pedidoRepository.save(pedido);

        // 9. Mapear la entidad Pedido guardada a un DTO de respuesta (PedidoResponse) y retornarlo.
        return pedidoMapper.toResponse(guardado,null);
    }

    @Transactional()
    public List<PedidoResponse> listarPedidosAsignadosAlUsuario() {
        // 1. Obtener el usuario autenticado desde el contexto de seguridad.
        CustomUserDetails auth = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long usuarioId = auth.getId();

        // 2. Buscar todos los IDs de clientes que están asignados y activos para el usuario actual.
        List<Long> clienteIds = usuarioClienteRepository.findClienteIdsActivosByUsuario(usuarioId);
        // Si el usuario no tiene clientes asignados, retornar una lista vacía inmediatamente.
        if (clienteIds.isEmpty()) return List.of();

        // 3. Obtener todos los pedidos asociados a la lista de IDs de clientes.
        List<Pedido> pedidos = pedidoRepository.findByCliente_IdIn(clienteIds);
        // Si no se encuentran pedidos para esos clientes, retornar una lista vacía.
        if (pedidos.isEmpty()) return List.of();

        // --- Optimización: Carga masiva de datos relacionados para evitar N+1 queries ---

        // 4. Extraer los IDs de los pedidos para buscar sus cobranzas asociadas en una sola consulta.
        List<Long> pedidoIds = pedidos.stream().map(Pedido::getId).toList();
        List<Cobranza> cobranzas = cobranzaRepository.findByPedidoIds(pedidoIds);
        // Crear un mapa para acceder a la cobranza de un pedido por su ID de forma eficiente (O(1)).
        Map<Long, Cobranza> cobranzaByPedidoId = cobranzas.stream()
                .collect(Collectors.toMap(c -> c.getPedido().getId(), Function.identity()));

        // 5. Extraer los IDs de las cobranzas para sumar los montos de sus pagos en una sola consulta.
        List<Long> cobranzaIds = cobranzas.stream().map(Cobranza::getId).toList();
        Map<Long, BigDecimal> sumPagosByCobranza = new HashMap<>();
        if (!cobranzaIds.isEmpty()) {
            // El repositorio devuelve una lista de [cobranzaId, suma_de_montos].
            for (Object[] row : pagoRepository.sumMontosActivosByCobranzaIds(cobranzaIds)) {
                Long cId = (Long) row[0];
                BigDecimal sum = (BigDecimal) row[1];
                // Guardar la suma de pagos por cada ID de cobranza.
                sumPagosByCobranza.put(cId, sum != null ? sum : BigDecimal.ZERO);
            }
        }

        // 6. Mapear cada entidad Pedido a su DTO de respuesta (PedidoResponse).
        List<PedidoResponse> responses = pedidos.stream().map(p -> {
            CobranzaDTO cobranzaDTO = null;
            // Buscar si existe una cobranza para el pedido actual en el mapa.
            Cobranza c = cobranzaByPedidoId.get(p.getId());
            if (c != null) {
                // Si hay cobranza, obtener el total pagado desde el mapa de pagos.
                BigDecimal pagado = sumPagosByCobranza.getOrDefault(c.getId(), BigDecimal.ZERO);
                BigDecimal total = c.getMontoTotal() != null ? c.getMontoTotal() : BigDecimal.ZERO;
                // Calcular el saldo pendiente.
                BigDecimal saldo = total.subtract(pagado);

                // Crear el DTO con el resumen de la cobranza.
                cobranzaDTO = new CobranzaDTO(
                        c.getId(),
                        c.getEstado(),
                        total,
                        pagado,
                        saldo
                );
            }

            // Usar el mapper para convertir el Pedido y su CobranzaDTO (si existe) a un PedidoResponse.
            return pedidoMapper.toResponse(p, cobranzaDTO);
        }).toList();

        // 7. Definir un orden de prioridad personalizado para los estados de los pedidos.
        Map<String, Integer> ordenEstados = Map.of(
                "REGISTRADO", 0,
                "PENDIENTE", 1,
                "EN_COBRANZA", 2,
                "PARCIAL", 3,
                "COMPLETADO", 4,
                "ANULADO", 5
        );

        // 8. Crear un comparador para ordenar la lista de respuestas.
        Comparator<PedidoResponse> cmp = Comparator
                // Primero, ordena por el valor numérico del estado definido en el mapa 'ordenEstados'.
                .comparingInt((PedidoResponse x) ->
                        ordenEstados.getOrDefault(
                                (x.estado() != null && x.estado().nombre() != null)
                                        ? x.estado().nombre().toUpperCase()
                                        : "",
                                Integer.MAX_VALUE)) // Los estados no encontrados van al final.
                // Como segundo criterio, ordena por fecha de creación de forma descendente (los más nuevos primero).
                .thenComparing(PedidoResponse::fechaCreacion,
                        Comparator.nullsLast(Comparator.reverseOrder()));

        // 9. Aplicar el ordenamiento y devolver la lista final.
        return responses.stream().sorted(cmp).toList();
    }



    @Transactional
    @Override
    public PedidoResponse actualizarPedido(PedidoRequest pedidoRequest) {
        // 1. Validación inicial: El ID del pedido es requerido para una actualización.
        if (pedidoRequest.id() == null) {
            throw new ApiException("El ID del pedido es obligatorio para actualizar.", HttpStatus.BAD_REQUEST);
        }

        // 2. Obtener el usuario autenticado que realiza la operación.
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long usuarioId = user.getId();

        // 3. Cargar el pedido existente desde la base de datos.
        // Si no se encuentra, se lanza una excepción 404 Not Found.
        Pedido pedido = pedidoRepository.findById(pedidoRequest.id())
                .orElseThrow(() -> new ApiException("Pedido no encontrado con ID: " + pedidoRequest.id(), HttpStatus.NOT_FOUND));

        // 4. Validar que el cliente del pedido esté asignado al usuario que realiza la modificación.
        if (!validarAsignacionCliente(usuarioId, pedidoRequest.clienteId())) {
            throw new ApiException("No tiene asignado este cliente", HttpStatus.NOT_FOUND);
        }

        // 5. Regla de negocio: No se pueden modificar pedidos que ya están en un estado final (completado o anulado).
        if ("Completado".equalsIgnoreCase(pedido.getEstado().getNombre()) || "Anulado".equalsIgnoreCase(pedido.getEstado().getNombre())) {
            throw new ApiException("No se puede modificar un pedido completado o anulado", HttpStatus.CONFLICT);
        }

        // 6. Actualizar campos simples del pedido, como las observaciones.
        pedido.setObservaciones(pedidoRequest.observaciones());

        // --- Lógica de actualización de detalles (Crear, Actualizar, Eliminar) ---
        List<DetallePedido> detallesActuales = pedido.getDetalles(); // Detalles existentes en la BD.
        List<DetallePedido> nuevosDetalles = new ArrayList<>();

        // 7. Obtener una lista de los IDs de detalles que vienen en la solicitud.
        // Esto servirá para identificar qué detalles existentes fueron eliminados.
        List<Long> idsRecibidos = pedidoRequest.detallePedido().stream()
                .map(DetallePedidoRequest::id)
                .filter(Objects::nonNull)
                .toList();

        boolean todosCompletos = true;


        // 8. Iterar sobre los detalles enviados en la solicitud para procesar creaciones y actualizaciones.
        for (DetallePedidoRequest d : pedidoRequest.detallePedido()) {
            // Cargar entidades relacionadas y preparar datos.
            TipoAve tipoAve = tipoAveRepository.findById(d.tipoAveId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de ave no encontrado con ID: " + d.tipoAveId()));
            // Asegurar que peso y precio no sean nulos para evitar NullPointerException.
            BigDecimal peso = d.peso() != null ? d.peso() : BigDecimal.ZERO;
            BigDecimal precio = d.precioXKilo() != null ? d.precioXKilo() : BigDecimal.ZERO;

            // 8.1. Si el detalle tiene un ID, es una ACTUALIZACIÓN de un detalle existente.
            if (d.id() != null) {
                // Buscar el detalle existente en la lista cargada de la BD.
                DetallePedido existente = detallesActuales.stream()
                        .filter(dp -> dp.getId().equals(d.id()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("Detalle no encontrado con ID: " + d.id()));

                // Actualizar sus propiedades.
                existente.setTipoAve(tipoAve);
                existente.setCantidadPollo(d.cantidad());
                existente.setPeso(peso);
                existente.setPrecioXKilo(precio);

                // Recalcular el monto estimado.
                BigDecimal montoEstimado = peso.multiply(precio);
                existente.setMontoEstimado(montoEstimado);
                existente.setTipoMerma(d.tipoMerma());
                existente.setOpDirecta(d.opDirecta());
                existente.setMermaKg(d.mermaKg());
                existente.setEstado(1);
                nuevosDetalles.add(existente);
            } else { // 8.2. Si el detalle NO tiene ID, es una CREACIÓN de un nuevo detalle.
                DetallePedido nuevo = new DetallePedido();
                nuevo.setPedido(pedido); // Asociar al pedido principal.
                nuevo.setTipoAve(tipoAve);
                nuevo.setCantidadPollo(d.cantidad());
                nuevo.setPeso(peso);
                nuevo.setPrecioXKilo(precio);

                // Calcular el monto estimado.
                BigDecimal montoEstimado = peso.multiply(precio);
                nuevo.setMontoEstimado(montoEstimado);
                nuevo.setTipoMerma(d.tipoMerma());
                nuevo.setOpDirecta(d.opDirecta());
                nuevo.setMermaKg(d.mermaKg());
                nuevo.setEstado(1); // Marcar como activo.
                nuevosDetalles.add(nuevo);
            }

            boolean detalleCompleto = d.cantidad() != null
                    && d.mermaKg() != null
                    && d.opDirecta() != null
                    && d.peso() != null
                    && d.precioXKilo() != null
                    && d.tipoMerma() != null;

            if (!detalleCompleto) {
                todosCompletos = false;
            }
        }

        // 9. Procesar ELIMINACIONES (soft delete).
        // Iterar sobre los detalles que estaban originalmente en la BD.
        for (DetallePedido actual : detallesActuales) {
            // Si un detalle existente no vino en la lista de IDs de la solicitud, se marca como inactivo.
            if (actual.getId() != null && !idsRecibidos.contains(actual.getId())) {
                actual.setEstado(0); // Estado 0 = Inactivo.
                nuevosDetalles.add(actual);
            }
        }

        // 10. Reemplazar la colección de detalles del pedido con la nueva lista procesada.
        pedido.setDetalles(nuevosDetalles);




        // guardar cantidad y precio total
        List<DetallePedido> activos = nuevosDetalles.stream()
                .filter(dp -> dp.getEstado() != null && dp.getEstado() == 1)
                .toList();

        BigDecimal importeTotal = activos.stream()
                .map(dp -> {
                    BigDecimal peso     = dp.getPeso() != null ? dp.getPeso() : BigDecimal.ZERO;
                    BigDecimal precio   = dp.getPrecioXKilo() != null ? dp.getPrecioXKilo() : BigDecimal.ZERO;
                    BigDecimal merma    = dp.getMermaKg() != null ? dp.getMermaKg() : BigDecimal.ZERO;
                    int cantidad        = dp.getCantidadPollo() != null ? dp.getCantidadPollo() : 0;

                    if (Boolean.TRUE.equals(dp.getOpDirecta())) {
                        return peso.multiply(precio)
                                .add(merma.multiply(BigDecimal.valueOf(cantidad)).multiply(precio));
                    } else {
                        return peso.multiply(precio);
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setImporteTotal(importeTotal);
        pedido.setCantidadDetalles(activos.size());


        Estado estadoPedido = estadoRepository.findById(todosCompletos ? 2L : 1L)
                .orElseThrow(() -> new ApiException("Estado no encontrado", HttpStatus.NOT_FOUND));
        pedido.setEstado(estadoPedido);





        // === Reglas de negocio post-procesamiento ===

        // 11. Verificar si el estado del pedido debe cambiar automáticamente.
        // Se comprueba si todos los detalles ACTIVOS tienen peso y precio.
        /* boolean todosCompletos = nuevosDetalles.stream()
                .filter(dp -> dp.getEstado() != null && dp.getEstado() == 1) // Solo considerar detalles activos.
                .allMatch(dp ->
                        dp.getPeso() != null && dp.getPeso().compareTo(BigDecimal.ZERO) > 0 &&
                                dp.getPrecioXKilo() != null && dp.getPrecioXKilo().compareTo(BigDecimal.ZERO) > 0
                );

        // Si todos los detalles están completos y el pedido estaba 'Pendiente', se cambia a 'Registrado'.
        if (todosCompletos && "Pendiente".equalsIgnoreCase(pedido.getEstado().getNombre())) {
            Estado registrado = estadoRepository.findByNombreIgnoreCase("Registrado")
                    .orElseThrow(() -> new ApiException("Estado 'Registrado' no configurado", HttpStatus.CONFLICT));
            pedido.setEstado(registrado);
        }*/

        // 12. Guardar el pedido y todos sus detalles (actualizados, nuevos e inactivados) en la BD.
        Pedido guardado = pedidoRepository.save(pedido);

        // 13. Preparar el DTO de respuesta, incluyendo la información de cobranza si existe.
        CobranzaDTO cobranzaDTO = null;
        Optional<Cobranza> cobranzaOpt = cobranzaRepository.findByPedidoId(guardado.getId());
        if (cobranzaOpt.isPresent()) {
            Cobranza c = cobranzaOpt.get();

            // Sumar los pagos activos para calcular el saldo.
            BigDecimal pagado = pagoRepository.sumMontosActivosByCobranzaId(c.getId());
            if (pagado == null) pagado = BigDecimal.ZERO;

            BigDecimal total = c.getMontoTotal() != null ? c.getMontoTotal() : BigDecimal.ZERO;
            BigDecimal saldo = total.subtract(pagado);

            // Construir el DTO de cobranza.
            cobranzaDTO = new CobranzaDTO(
                    c.getId(),
                    c.getEstado(),
                    total,
                    pagado,
                    saldo
            );
        }
        // 14. Mapear la entidad Pedido guardada a un DTO de respuesta y retornarlo.
        return pedidoMapper.toResponse(guardado, cobranzaDTO);

    }

    @Override
    public boolean validarAsignacionCliente(Long usuarioId, Long clienteId){
        return usuarioClienteRepository.existsByUsuarioIdAndClienteIdAndEstado(usuarioId, clienteId, 'A');
    }

    @Transactional
    @Override
    public PedidoResponse confirmarPedido(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ApiException("Pedido no encontrado con ID: " + pedidoId, HttpStatus.NOT_FOUND));

        if (pedido.getEstado() == null || pedido.getEstado().getId() != 2L) {
            throw new ApiException("Solo se pueden confirmar pedidos en estado 'Por confirmar'", HttpStatus.CONFLICT);
        }

        Estado confirmado = estadoRepository.findById(3L)
                .orElseThrow(() -> new ApiException("Estado 'Confirmado' no configurado", HttpStatus.NOT_FOUND));

        pedido.setEstado(confirmado);

        Pedido pedidoSave = pedidoRepository.save(pedido);
        return pedidoMapper.toResponse(pedidoSave, null);
    }


}
