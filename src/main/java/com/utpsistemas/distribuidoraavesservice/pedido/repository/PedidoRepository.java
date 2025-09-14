package com.utpsistemas.distribuidoraavesservice.pedido.repository;

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
           SELECT p
           FROM Pedido p
           JOIN FETCH p.cliente c
           WHERE p.usuario.id = :usuarioId
             AND p.estado.id = :estadoId
           """)
    List<Pedido> findByUsuarioIdAndEstadoId(@Param("usuarioId") Long usuarioId,
                                            @Param("estadoId") Integer estadoId);
}
