package com.utpsistemas.distribuidoraavesservice.usuario.service;

import com.utpsistemas.distribuidoraavesservice.auth.entity.Rol;
import com.utpsistemas.distribuidoraavesservice.auth.entity.Usuario;
import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import com.utpsistemas.distribuidoraavesservice.auth.repository.RolRepository;
import com.utpsistemas.distribuidoraavesservice.auth.repository.UsuarioRepository;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioRequest;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.UsuarioResponse;
import com.utpsistemas.distribuidoraavesservice.usuario.mapper.UsuarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapper::usuarioToUsuarioResponse)
                .toList();
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        return null;
    }

    @Override
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email()))
            throw new ApiException("Ya existe un usuario con el email: " + request.email(), HttpStatus.CONFLICT);

        List<Rol> roles = getActiveRolesByIds(request.rolesIds());

        if (roles.isEmpty()) {
            throw new ApiException("El rol asignado esta inactivo o no existe", HttpStatus.CONFLICT);
        }

        Usuario usuario = UsuarioMapper.usuarioRequestToUsuario(request, roles);
        usuario.setPassword(passwordEncoder.encode(request.password()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return UsuarioMapper.usuarioToUsuarioResponse(usuarioGuardado);
    }

    @Override
    public UsuarioResponse actualizarUsuario(UsuarioRequest request) {
        try{

        if (request.id() == null)
            throw new ApiException("Debe enviar el ID del usuario a actualizar.", HttpStatus.CONFLICT);

        Usuario usuario = usuarioRepository.findById(request.id())
                .orElseThrow(() -> new ApiException("Usuario no encontrado con ID: " + request.id(), HttpStatus.CONFLICT));

        if (request.email() != null && !request.email().equalsIgnoreCase(usuario.getEmail())) {
            boolean emailExiste = usuarioRepository.existsByEmail(request.email());
            if (emailExiste) {
                throw new ApiException("Ya existe un usuario con el email: " + request.email(), HttpStatus.CONFLICT);
            }
            usuario.setEmail(request.email());
        }

        if (request.nombres() != null) {
            usuario.setNombres(request.nombres());
        }

        if (request.password() != null) {
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.estado() != null) {
            usuario.setEstado(request.estado());
        }

        if (request.rolesIds() != null) {
            List<Rol> roles = getActiveRolesByIds(request.rolesIds());
            usuario.setRoles(roles);
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return UsuarioMapper.usuarioToUsuarioResponse(usuarioActualizado);

        }catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    private List<Rol> getActiveRolesByIds(List<Long> ids) {
        return rolRepository.findAllById(ids).stream()
                .filter(rol -> rol.getEstado() != null && rol.getEstado() == 'A')
                .collect(Collectors.toList());
    }
}
