package com.utpsistemas.distribuidoraavesservice.cobranza.service;

import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import com.utpsistemas.distribuidoraavesservice.auth.security.CustomUserDetails;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.*;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.*;
import com.utpsistemas.distribuidoraavesservice.cobranza.enums.TipoPedidoCobranzaMovimiento;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.*;
import com.utpsistemas.distribuidoraavesservice.pedido.repository.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

@Slf4j
@Service
@AllArgsConstructor
class CobranzaMovimientoServiceImpl implements CobranzaMovimientoService {

    private final CobranzaMovimientoRepository cobranzaMovimientoRepository;
    private final PedidoRepository pedidoRepository;
    private final CobranzaMovimientoCategoriaRepository cobranzaMovimientoCategoriaRepository;


    @Override
    @Transactional
    public MovimientoMasivoResponse crearMovimientosMasivos(MovimientoMasivoRequest req) {
        if (req.tipo() == null || (req.tipo() != 1 && req.tipo() != 2)) {
            throw new ApiException("tipo inválido: use 1=Descuento, 2=Pago");
        }
        if (req.pedidoIds() == null || req.pedidoIds().isEmpty()) {
            throw new ApiException("pedidoIds es requerido y no puede estar vacío");
        }

        var categoria = cobranzaMovimientoCategoriaRepository.findById(req.categoriaId())
                .orElseThrow(() -> new ApiException("Categoría no encontrada: " + req.categoriaId()));

        var pedidos = pedidoRepository.findAllForUpdateByIdInOrderByFechaCreacionAsc(req.pedidoIds());
        if (pedidos.isEmpty()) {
            throw new ApiException("Ningún pedido hallado para los IDs enviados");
        }

        var totalAplicable = pedidos.stream()
                .map(p -> nz(p.getTotalImporte())
                        .subtract(nz(p.getTotalDescuento()))
                        .subtract(nz(p.getTotalPagado())))
                .filter(s -> s.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // bloquear excedENTE:
        if (req.monto().compareTo(totalAplicable) > 0) {
            throw new ApiException(
                    "El monto excede el saldo total de los pedidos por " +
                            req.monto().subtract(totalAplicable).setScale(2, RoundingMode.HALF_UP)
            );
        }


        // Usuario actual (para auditoría)
        var auth = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long usrId = auth.getId();

        var remaining = req.monto().setScale(2, RoundingMode.HALF_UP);
        var aplicados = new ArrayList<MovimientoAplicadoItem>();
        var movimientosToSave = new ArrayList<PedidoCobranzaMovimiento>();

        for (var p : pedidos) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            var totalImporte     = nz(p.getTotalImporte());
            var totalPagado      = nz(p.getTotalPagado());
            var totalDescuento   = nz(p.getTotalDescuento());

            // Saldo pendiente actual
            var saldoPendiente = totalImporte.subtract(totalDescuento).subtract(totalPagado);
            if (saldoPendiente.compareTo(BigDecimal.ZERO) <= 0) continue;

            var aplicar = remaining.min(saldoPendiente);
            if (aplicar.compareTo(BigDecimal.ZERO) <= 0) continue;

            // Crear movimiento con 'aplicar'
            var mov = new PedidoCobranzaMovimiento();
            mov.setPedido(p);
            mov.setTipo(req.tipo());
            mov.setCategoria(categoria);
            mov.setMonto(aplicar);
            mov.setObservacion(req.observacion());
            mov.setEstado('A');
            mov.setCreatedBy(usrId);
            movimientosToSave.add(mov);

            // Actualizar denormalizados
            if (req.tipo() == 2) { // PAGO
                totalPagado = totalPagado.add(aplicar);
                p.setTotalPagado(totalPagado);
            } else {               // DESCUENTO
                totalDescuento = totalDescuento.add(aplicar);
                p.setTotalDescuento(totalDescuento);
            }
            var nuevoSaldo = totalImporte.subtract(totalDescuento).subtract(totalPagado);
            if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                nuevoSaldo = BigDecimal.ZERO;
            }
            p.setTotalSaldo(nuevoSaldo);

            // Persistir pedido actualizado (depende de tu cascada; explicit save si lo prefieres)
            // pedidoRepository.save(p);

            // Registrar detalle
            aplicados.add(new MovimientoAplicadoItem(
                    p.getId(),
                    null, // se seteará después de guardar los movimientos
                    aplicar,
                    p.getTotalPagado(),
                    p.getTotalDescuento(),
                    p.getTotalSaldo()
            ));

            remaining = remaining.subtract(aplicar);
        }

        // Guardar movimientos en batch
        var saved = cobranzaMovimientoRepository.saveAll(movimientosToSave);

        // Los pedidos se persisten por dirty checking al commit de la @Transactional
        //pedidoRepository.saveAll(pedidos);
        //pedidoRepository.flush();

        // Copiar IDs guardados a la lista de respuesta (mismo orden)
        for (int i = 0; i < aplicados.size(); i++) {
            var item = aplicados.get(i);
            var mov  = saved.get(i);
            aplicados.set(i, new MovimientoAplicadoItem(
                    item.pedidoId(),
                    mov.getId(),
                    item.aplicado(),
                    item.nuevoTotalPagado(),
                    item.nuevoTotalDescuento(),
                    item.nuevoSaldo()
            ));
        }

        var aplicadoTotal = req.monto().subtract(remaining).max(BigDecimal.ZERO);

        var saldoRestante = pedidos.stream()
                .map(p -> {
                    var totalImporte   = nz(p.getTotalImporte());
                    var totalPagado    = nz(p.getTotalPagado());
                    var totalDescuento = nz(p.getTotalDescuento());
                    var saldo = totalImporte.subtract(totalDescuento).subtract(totalPagado);
                    return saldo.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : saldo;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new MovimientoMasivoResponse(
                req.tipo(),
                req.categoriaId(),
                req.monto(),
                aplicadoTotal,
                remaining.max(BigDecimal.ZERO),
                saldoRestante,
                aplicados
        );
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v.setScale(2, RoundingMode.HALF_UP);
    }

}
