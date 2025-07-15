package com.utpsistemas.distribuidoraavesservice.auth.service;

import com.utpsistemas.distribuidoraavesservice.auth.dto.LoginRequest;
import com.utpsistemas.distribuidoraavesservice.auth.dto.LoginResponse;
import com.utpsistemas.distribuidoraavesservice.auth.entity.Rol;
import com.utpsistemas.distribuidoraavesservice.auth.entity.Usuario;
import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import com.utpsistemas.distribuidoraavesservice.auth.helper.JwtUtil;
import com.utpsistemas.distribuidoraavesservice.auth.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtils;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailAndEstado(request.email(), 'A')
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado o inactivo: {}", request.email());
                    return new ApiException("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
                });

        if (!passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new ApiException("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        List<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .toList();

        String token = jwtUtils.generateToken(usuario.getEmail(), roles);
        log.info("Login exitoso para: {} con roles: {}", usuario.getEmail(), roles);
        return new LoginResponse(token, roles);
    }
}
