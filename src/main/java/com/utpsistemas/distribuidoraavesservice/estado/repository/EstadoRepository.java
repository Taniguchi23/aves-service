package com.utpsistemas.distribuidoraavesservice.estado.repository;

import com.utpsistemas.distribuidoraavesservice.estado.entity.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoRepository extends JpaRepository<Estado,Long> {
    Optional<Estado> findByNombreIgnoreCase(String nombre);
}
