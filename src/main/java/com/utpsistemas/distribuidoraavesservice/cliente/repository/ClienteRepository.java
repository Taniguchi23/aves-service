package com.utpsistemas.distribuidoraavesservice.cliente.repository;

import com.utpsistemas.distribuidoraavesservice.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByNumeroDocumento(String numeroDocumento);
}
