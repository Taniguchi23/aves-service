package com.utpsistemas.distribuidoraavesservice.usuario.service;

import com.utpsistemas.distribuidoraavesservice.auth.entity.Usuario;
import com.utpsistemas.distribuidoraavesservice.auth.repository.UsuarioRepository;
import com.utpsistemas.distribuidoraavesservice.cliente.entity.Cliente;
import com.utpsistemas.distribuidoraavesservice.cliente.entity.UsuarioCliente;
import com.utpsistemas.distribuidoraavesservice.cliente.repository.ClienteRepository;
import com.utpsistemas.distribuidoraavesservice.cliente.repository.UsuarioClienteRepository;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.AsignacionClienteRequest;
import com.utpsistemas.distribuidoraavesservice.usuario.dto.AsignacionClienteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioClienteServiceImpl implements UsuarioClienteService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioClienteRepository usuarioClienteRepository;




    @Override
    public AsignacionClienteResponse asignarClientes(AsignacionClienteRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Long> nuevosIds = request.clienteIds();

        List<UsuarioCliente> actuales = usuarioClienteRepository.findByUsuarioId(usuario.getId());

        for (UsuarioCliente uc : actuales) {
            if (nuevosIds.contains(uc.getCliente().getId())) {
                uc.setEstado('A');
            } else {
                uc.setEstado('I');
            }
        }

        List<Long> yaAsignados = actuales.stream()
                .map(uc -> uc.getCliente().getId())
                .toList();

        List<Cliente> nuevosClientes = clienteRepository.findAllById(nuevosIds).stream()
                .filter(c -> !yaAsignados.contains(c.getId()))
                .toList();

        List<Long> idsEncontrados = nuevosClientes.stream().map(Cliente::getId).toList();
        List<Long> idsInvalidos = nuevosIds.stream()
                .filter(id -> !idsEncontrados.contains(id) && !yaAsignados.contains(id))
                .toList();

        if (!idsInvalidos.isEmpty()) {
            throw new RuntimeException("Los siguientes IDs de cliente no existen: " + idsInvalidos);
        }

        List<UsuarioCliente> nuevos = nuevosClientes.stream()
                .map(cliente -> {
                    UsuarioCliente uc = new UsuarioCliente();
                    uc.setUsuario(usuario);
                    uc.setCliente(cliente);
                    uc.setEstado('A');
                    return uc;
                }).toList();

        usuarioClienteRepository.saveAll(actuales);
        usuarioClienteRepository.saveAll(nuevos);

        List<Cliente> clientesAsignados = usuarioClienteRepository.findByUsuarioId(usuario.getId()).stream()
                .filter(uc -> uc.getEstado() != null && uc.getEstado() == 'A')
                .map(UsuarioCliente::getCliente)
                .toList();

        List<AsignacionClienteResponse.ClienteAsignado> clientesDto = clientesAsignados.stream()
                .map(c -> new AsignacionClienteResponse.ClienteAsignado(
                        c.getId(),
                        c.getNombres(),
                        c.getNumeroDocumento(),
                        c.getTelefono(),
                        c.getDireccion()
                )).toList();

        return new AsignacionClienteResponse(
                usuario.getId(),
                usuario.getNombres(),
                usuario.getEmail(),
                clientesDto
        );
    }
}
