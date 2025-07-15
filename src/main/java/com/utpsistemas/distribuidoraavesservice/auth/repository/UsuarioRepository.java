package com.utpsistemas.distribuidoraavesservice.auth.repository;

import com.utpsistemas.distribuidoraavesservice.auth.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByEmailAndEstado(String username, Character estado);
}
