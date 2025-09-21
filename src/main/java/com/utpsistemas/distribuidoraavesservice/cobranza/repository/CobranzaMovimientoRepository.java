package com.utpsistemas.distribuidoraavesservice.cobranza.repository;

import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Cobranza;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.PedidoCobranzaMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CobranzaMovimientoRepository extends JpaRepository<PedidoCobranzaMovimiento, Long> {
    boolean existsByPedidoId(Long pedidoId);

    @Query("""
    select m
    from PedidoCobranzaMovimiento m
      join fetch m.categoria
    where m.pedido.id in :pedidoIds
    """)
    List<PedidoCobranzaMovimiento> findByPedidoIds(@Param("pedidoIds") List<Long> pedidoIds);

}
