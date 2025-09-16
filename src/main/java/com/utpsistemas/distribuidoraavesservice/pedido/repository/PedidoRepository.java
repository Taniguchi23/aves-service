package com.utpsistemas.distribuidoraavesservice.pedido.repository;

import com.utpsistemas.distribuidoraavesservice.cobranza.projection.CobranzaClienteResumenProjection;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {
    List<Pedido> findByClienteIdAndEstadoIn(Long clienteId, List<String> estados);

    @EntityGraph(attributePaths = {"cliente", "usuario", "estado", "detalles"})
    List<Pedido> findByCliente_IdIn(Collection<Long> clienteIds);

    @Query("""
           SELECT c.id                       AS clienteId,
                  c.nombres                  AS clienteNombre,
                  SUM(COALESCE(p.importeTotal, 0)) AS importeTotal,
                  COUNT(p)                   AS cantidadPedidos,
                  e.id                       AS estadoId,
                  e.nombre                   AS estadoNombre,
                  MAX(p.fechaCreacion)       AS fechaMax
           FROM Pedido p
           JOIN p.cliente c
           JOIN p.estado e
           WHERE p.usuario.id = :usuarioId
             AND e.id = :estadoId
           GROUP BY c.id, c.nombres, e.id, e.nombre
           ORDER BY c.nombres
           """)
    List<CobranzaClienteResumenProjection> resumirPorUsuarioYEstado(@Param("usuarioId") Long usuarioId,
                                                                    @Param("estadoId") Integer estadoId);


}
