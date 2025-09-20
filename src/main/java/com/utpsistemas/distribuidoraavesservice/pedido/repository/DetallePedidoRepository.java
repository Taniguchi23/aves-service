package com.utpsistemas.distribuidoraavesservice.pedido.repository;

import com.utpsistemas.distribuidoraavesservice.pedido.entity.PedidoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<PedidoDetalle, Long> {
}
