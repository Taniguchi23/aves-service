package com.utpsistemas.distribuidoraavesservice.cobranza.repository;

import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Cobranza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CobranzaRepository extends JpaRepository<Cobranza, Long> {
    boolean existsByPedidoId(Long pedidoId);

    @Query("SELECT c FROM Cobranza c JOIN FETCH c.pedido p JOIN FETCH p.cliente")
    List<Cobranza> findAllWithPedidoAndCliente();
}
