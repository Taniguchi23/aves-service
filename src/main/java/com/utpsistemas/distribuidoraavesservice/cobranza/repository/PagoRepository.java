package com.utpsistemas.distribuidoraavesservice.cobranza.repository;

import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago,Long> {
    @Query("SELECT p FROM Pago p WHERE p.cobranza.id = :cobranzaId AND p.estado = 'A'")
    List<Pago> findAllActivosByCobranzaId(@Param("cobranzaId") Long cobranzaId);

}
