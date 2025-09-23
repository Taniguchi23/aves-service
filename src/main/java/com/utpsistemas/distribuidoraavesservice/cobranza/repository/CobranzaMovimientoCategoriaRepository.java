package com.utpsistemas.distribuidoraavesservice.cobranza.repository;

import com.utpsistemas.distribuidoraavesservice.cobranza.entity.PedidoCobranzaMovimientoCategoria;
import com.utpsistemas.distribuidoraavesservice.cobranza.enums.TipoPedidoCobranzaMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CobranzaMovimientoCategoriaRepository extends JpaRepository<PedidoCobranzaMovimientoCategoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    List<PedidoCobranzaMovimientoCategoria> findByTipo(TipoPedidoCobranzaMovimiento tipoPedidoCobranzaMovimiento);
}

