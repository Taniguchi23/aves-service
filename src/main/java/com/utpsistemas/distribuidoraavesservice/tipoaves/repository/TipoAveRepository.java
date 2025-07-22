package com.utpsistemas.distribuidoraavesservice.tipoaves.repository;

import com.utpsistemas.distribuidoraavesservice.tipoaves.entity.TipoAve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoAveRepository extends JpaRepository<TipoAve,Integer> {
    boolean existsByNombreIgnoreCase(String nombre);
}
