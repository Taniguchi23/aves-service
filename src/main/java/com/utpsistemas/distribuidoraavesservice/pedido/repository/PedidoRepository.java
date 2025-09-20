package com.utpsistemas.distribuidoraavesservice.pedido.repository;

import com.utpsistemas.distribuidoraavesservice.cobranza.projection.CobranzaClienteResumenProjection;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import com.utpsistemas.distribuidoraavesservice.pedido.projection.PedidoDetalleProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {
    List<Pedido> findByClienteIdAndEstadoIn(Long clienteId, List<String> estados);

    @EntityGraph(attributePaths = {"cliente", "usuario", "estado", "detalles"})
    List<Pedido> findByCliente_IdIn(Collection<Long> clienteIds);

    @Query("""
       SELECT c.id                           AS clienteId,
              c.nombres                      AS clienteNombre,
              SUM(COALESCE(p.importeTotal, 0)) AS importeTotal,
              COUNT(p)                       AS cantidadPedidos,
              e.id                           AS estadoId,
              e.nombre                       AS estadoNombre,
              MAX(p.fechaCreacion)           AS fechaMax
       FROM Pedido p
       JOIN p.cliente c
       JOIN p.estado e
       JOIN UsuarioCliente uc
            ON uc.cliente = c
           AND uc.usuario.id = :usuarioId
           AND (uc.estado IS NULL OR uc.estado = 'A')
       WHERE e.id IN :estadoIds
       GROUP BY c.id, c.nombres, e.id, e.nombre
       ORDER BY c.nombres
       """)
    List<CobranzaClienteResumenProjection> resumirPorUsuarioYEstados(
            @Param("usuarioId") Long usuarioId,
            @Param("estadoIds") List<Integer> estadoIds);





    @Query("""
    select p.id as pedidoId,
           cob.id as cobranzaId,
           cob.estado as cobranzaEstado,
           coalesce(cob.montoTotal, 0) as total,
           coalesce(sum(case when pa.estado = 'A' then pa.monto else 0 end), 0) as pagado
    from Pedido p
      join p.cliente c
      join UsuarioCliente uc
           on uc.cliente = c
          and uc.estado = 'A'
          and uc.usuario.id = :usuarioId
      left join Cobranza cob on cob.pedido = p
      left join Pago pa on pa.cobranza = cob
          where p.estado.id in :estadoIds
    group by p.id, cob.id, cob.estado, cob.montoTotal
    order by max(p.fechaCreacion) desc
    """)
    List<PedidoDetalleProjection> findAggByUsuario(@Param("usuarioId") Long usuarioId,
                                                   @Param("estadoIds") List<Integer> estadoIds);


    @Query("""
    select distinct p
    from Pedido p
      left join fetch p.cliente
      left join fetch p.estado
      left join fetch p.detalles d
      left join fetch d.tipoAve
    where p.id in :pedidoIds
    order by p.fechaCreacion desc
    """)
    List<Pedido> fetchPedidosConDetalles(@Param("pedidoIds") List<Long> pedidoIds);


    @Query("""
    select distinct p
    from Pedido p
      left join fetch p.cliente
      left join fetch p.usuario
      left join fetch p.estado
      left join fetch p.detalles d
      left join fetch d.tipoAve
    where p.id = :pedidoId
    """)
    Optional<Pedido> fetchPedidoConDetallesById(@Param("pedidoId") Long pedidoId);
}
