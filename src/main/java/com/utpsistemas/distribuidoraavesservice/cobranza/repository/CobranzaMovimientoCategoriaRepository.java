package com.utpsistemas.distribuidoraavesservice.cobranza.repository;

import com.utpsistemas.distribuidoraavesservice.cobranza.entity.PedidoCobranzaMovimientoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CobranzaMovimientoCategoriaRepository extends JpaRepository<PedidoCobranzaMovimientoCategoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}

