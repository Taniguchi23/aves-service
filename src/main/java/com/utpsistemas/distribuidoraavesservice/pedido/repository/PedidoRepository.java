package com.utpsistemas.distribuidoraavesservice.pedido.repository;

import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {
}
