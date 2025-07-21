package com.utpsistemas.distribuidoraavesservice.usuario.mapper;

import com.utpsistemas.distribuidoraavesservice.auth.entity.Rol;
import com.utpsistemas.distribuidoraavesservice.auth.entity.Usuario;
import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteRequest;
import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteResponse;
import com.utpsistemas.distribuidoraavesservice.cliente.entity.Cliente;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioRequest;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {
    public static UsuarioResponse usuarioToUsuarioResponse(Usuario usuario) {
        List<UsuarioResponse.RolDto> roles = usuario.getRoles().stream()
                .filter(rol -> rol.getEstado() != null && rol.getEstado() == 'A')
                .map(rol -> new UsuarioResponse.RolDto(rol.getId(), rol.getNombre()))
                .collect(Collectors.toList());

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombres(),
                usuario.getEmail(),
                usuario.getEstado(),
                roles
        );
    }

    public static Usuario usuarioRequestToUsuario(UsuarioRequest dto, List<Rol> rolesActivos) {
        Usuario usuario = new Usuario();
        usuario.setNombres(dto.nombres());
        usuario.setEmail(dto.email());
        usuario.setPassword(dto.password());
        usuario.setEstado(dto.estado());
        usuario.setRoles(rolesActivos);
        return usuario;
    }
}
