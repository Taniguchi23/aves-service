package com.utpsistemas.distribuidoraavesservice.pedido.repository;

import com.utpsistemas.distribuidoraavesservice.cobranza.projection.CobranzaClienteResumenProjection;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import com.utpsistemas.distribuidoraavesservice.pedido.projection.PedidoDetalleProjection;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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
        SELECT 
            c.id                                         AS clienteId,
            c.nombres                                    AS clienteNombre,
            SUM(COALESCE(p.totalImporte, 0))             AS totalImporte,
            COUNT(DISTINCT p)                             AS cantidadPedidos,
            e.id                                         AS estadoId,
            e.nombre                                     AS estadoNombre,
            MAX(p.fechaCreacion)                         AS fechaMax,
            SUM(COALESCE(p.totalPagado, 0))              AS totalPagado,
            SUM(COALESCE(p.totalDescuento, 0))           AS totalDescuento,
            SUM(COALESCE(p.totalSaldo, 0))               AS totalSaldo
        FROM Pedido p
        JOIN p.cliente c
        JOIN p.estado e
        JOIN UsuarioCliente uc 
            ON uc.cliente = c 
           AND uc.usuario.id = :usuarioId
           AND (uc.estado = 'A')
        WHERE e.id IN :estadoIds
        GROUP BY c.id, c.nombres, e.id, e.nombre
        ORDER BY c.nombres
    """)
    List<CobranzaClienteResumenProjection> resumirPorUsuarioYEstados(
            @Param("usuarioId") Long usuarioId,
            @Param("estadoIds") List<Integer> estadoIds);


/*
    @Query("""
select p.id as pedidoId
from Pedido p
join p.cliente c
where p.estado.id in :estadoIds
  and exists (
       select 1 from UsuarioCliente uc
       where uc.cliente = c
         and uc.usuario.id = :usuarioId
         and uc.estado = 'A'
  )
order by p.fechaCreacion desc
""")
    List<Long> findPedidoIdsByUsuario(
            @Param("usuarioId") Long usuarioId,
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
*/

    @Query("""
    select distinct p
    from Pedido p
      join fetch p.cliente c
      join fetch p.estado  e
      left join fetch p.detalles d
      left join fetch d.tipoAve
    where e.id in :estadoIds
      and exists (
           select 1 from UsuarioCliente uc
           where uc.cliente = c
             and uc.usuario.id = :usuarioId
             and uc.estado = 'A'
      )
    order by p.fechaCreacion desc
    """)
    List<Pedido> fetchPedidosConDetallesPorUsuario(
            @Param("usuarioId") Long usuarioId,
            @Param("estadoIds") List<Integer> estadoIds);


    @Query("""
select distinct p
from Pedido p
  join fetch p.cliente c
  join fetch p.estado  e
  left join fetch p.detalles d
  left join fetch d.tipoAve
where p.id = :pedidoId
  and e.id in :estadoIds
  and exists (
       select 1 from UsuarioCliente uc
       where uc.cliente = c
         and uc.usuario.id = :usuarioId
         and uc.estado = 'A'
  )
""")
    Optional<Pedido> findPedidoConDetallesPorIdYUsuario(
            @Param("pedidoId") Long pedidoId,
            @Param("usuarioId") Long usuarioId,
            @Param("estadoIds") List<Integer> estadoIds);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select p
        from Pedido p
        where p.id in :ids
        order by p.fechaCreacion asc
     """)
    List<Pedido> findAllForUpdateByIdInOrderByFechaCreacionAsc(@Param("ids") List<Long> ids);

/*
    @Query("""
        select distinct p
        from Pedido p
         left join fetch p.movimientos m
         left join fetch m.categoria
        where p.id in :pedidoIds
        """)
    List<Pedido> fetchPedidosConMovimientos(@Param("pedidoIds") List<Long> pedidoIds);
*/

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

/*
    @Query("""
    select distinct p
    from Pedido p
      left join fetch p.cliente
      left join fetch p.estado
      left join fetch p.detalles d
      left join fetch d.tipoAve
      left join fetch p.movimientos m
      left join fetch m.categoria
    where p.id in :pedidoIds
    order by p.fechaCreacion desc
    """)
    List<Pedido> fetchPedidosConDetallesYMovimientos(@Param("pedidoIds") List<Long> pedidoIds);*/

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
    List<PedidoDetalleProjection> fetchPedidosConDetallesYMovimientos(@Param("usuarioId") Long usuarioId,
                                                   @Param("estadoIds") List<Long> estadoIds);

    @Query("""
    select distinct p
    from Pedido p
      join fetch p.cliente c
      join fetch p.estado  e
      left join fetch p.detalles d
      left join fetch d.tipoAve
    where e.id in :estadoIds
      and c.id = :clienteId
      and exists (
           select 1 from UsuarioCliente uc
           where uc.cliente = c
             and uc.usuario.id = :usuarioId
             and uc.estado = 'A'
      )
    order by p.fechaCreacion asc
    """)
    List<Pedido> fetchPedidosConDetallesPorUsuarioAndCliente(
            @Param("usuarioId") Long usuarioId,
            @Param("clienteId") Long clienteId,
            @Param("estadoIds") List<Integer> estadoIds);
}
