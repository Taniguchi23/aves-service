package com.utpsistemas.distribuidoraavesservice.pedido.repository;

import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {
    List<Pedido> findByClienteIdAndEstadoIn(Long clienteId, List<String> estados);
}
