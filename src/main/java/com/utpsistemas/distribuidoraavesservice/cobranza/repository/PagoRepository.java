package com.utpsistemas.distribuidoraavesservice.cobranza.repository;

import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago,Long> {
    @Query("SELECT p FROM Pago p WHERE p.cobranza.id = :cobranzaId AND p.estado = 'A'")
    List<Pago> findAllActivosByCobranzaId(@Param("cobranzaId") Long cobranzaId);

    @Query("""
        select p.cobranza.id as cobranzaId, coalesce(sum(p.monto), 0)
        from Pago p
        where p.estado = 'A'
          and p.cobranza.id in :cobranzaIds
        group by p.cobranza.id
    """)
    List<Object[]> sumMontosActivosByCobranzaIds(Collection<Long> cobranzaIds);

    @Query("SELECT COALESCE(SUM(p.monto), 0) " +
            "FROM Pago p " +
            "WHERE p.cobranza.id = :cobranzaId AND p.estado = 'Activo'")
    BigDecimal sumMontosActivosByCobranzaId(@Param("cobranzaId") Long cobranzaId);
}
