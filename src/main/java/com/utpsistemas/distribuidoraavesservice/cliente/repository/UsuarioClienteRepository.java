package com.utpsistemas.distribuidoraavesservice.cliente.repository;

import com.utpsistemas.distribuidoraavesservice.cliente.entity.Cliente;
import com.utpsistemas.distribuidoraavesservice.cliente.entity.UsuarioCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioClienteRepository extends JpaRepository<UsuarioCliente, Long> {
    @Query("SELECT uc.cliente FROM UsuarioCliente uc WHERE uc.usuario.id = :usuarioId AND uc.estado = :estado")
    List<Cliente> getClientes(Long usuarioId, Character estado);

    List<UsuarioCliente> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndClienteIdAndEstado(Long usuarioId, Long clienteId, Character estado);

    @Query("""
        select uc.cliente.id
        from UsuarioCliente uc
        where uc.usuario.id = :usuarioId
          and uc.estado = 'A'
    """)
    List<Long> findClienteIdsActivosByUsuario(Long usuarioId);
}
