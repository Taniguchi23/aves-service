package com.utpsistemas.distribuidoraavesservice.cliente.service;

import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import com.utpsistemas.distribuidoraavesservice.auth.helper.Hashid;
import com.utpsistemas.distribuidoraavesservice.auth.security.CustomUserDetails;
import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteRequest;
import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteResponse;
import com.utpsistemas.distribuidoraavesservice.cliente.entity.Cliente;
import com.utpsistemas.distribuidoraavesservice.cliente.mapper.ClienteMapper;
import com.utpsistemas.distribuidoraavesservice.cliente.repository.ClienteRepository;
import com.utpsistemas.distribuidoraavesservice.cliente.repository.UsuarioClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private UsuarioClienteRepository usuarioClienteRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private Hashid hashid;

    @Autowired
    private ClienteMapper clienteMapper;

    @Override
    public List<ClienteResponse> listarClientesActivos() {
        List<Cliente> listaCliente = getClientes(getUsuarioId(),'A');
        return listaCliente.stream()
                .map(clienteMapper::clienteToClienteResponse)
                .toList();
    }

    @Override
    public ClienteResponse obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ApiException("Cliente no encontrado", HttpStatus.NOT_FOUND));

        return clienteMapper.clienteToClienteResponse(cliente);
    }

    @Override
    public ClienteResponse crearCliente(ClienteRequest request) {
        if (clienteRepository.existsByNumeroDocumento(request.numeroDocumento()))
            throw new ApiException("Ya existe un cliente con ese número de documento", HttpStatus.CONFLICT);

        Cliente cliente = clienteMapper.clienteRequestToCliente(request);
        return clienteMapper.clienteToClienteResponse(clienteRepository.save(cliente));
    }

    @Override
    public ClienteResponse actualizarCliente(ClienteRequest request) {
        if (request.id() == null) throw new ApiException("El Id es requerido", HttpStatus.BAD_REQUEST);
        Cliente cliente = clienteRepository.findById(request.id().longValue())
                .orElseThrow(() -> new ApiException("Cliente no encontrado",HttpStatus.NOT_FOUND));

        if (!cliente.getNumeroDocumento().equals(request.numeroDocumento())) {
            if (clienteRepository.existsByNumeroDocumento(request.numeroDocumento()))
                throw new ApiException("Ya existe un cliente con ese número de documento", HttpStatus.CONFLICT);
        }

        if (request.nombres()!=null) cliente.setNombres(request.nombres());
        if (request.numeroDocumento()!=null) cliente.setNumeroDocumento(request.numeroDocumento());
        if (request.tipoDocumento()!=null) cliente.setTipoDocumento(request.tipoDocumento());
        if (request.telefono()!=null) cliente.setTelefono(request.telefono());
        if (request.apodo()!=null) cliente.setApodo(request.apodo());
        if (request.latitud()!=null) cliente.setLatitud(request.latitud());
        if (request.longitud()!=null) cliente.setLongitud(request.longitud());
        if (request.direccion()!=null) cliente.setDireccion(request.direccion());
        if (request.estado()!=null) cliente.setEstado(request.estado());

        Cliente clienteActualizado = clienteRepository.save(cliente);
        return clienteMapper.clienteToClienteResponse(clienteActualizado);
    }

    public List<ClienteResponse> listarClientesInactivos() {
        List<Cliente> listaCliente = getClientes(getUsuarioId(),'I');
        return listaCliente.stream()
                .map(clienteMapper::clienteToClienteResponse)
                .toList();
    }

    private List<Cliente> getClientes(Long idUsuario, Character estado) {
        return usuarioClienteRepository.getClientes(idUsuario, estado);
    }

    private Long getUsuarioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getId();
    }
}
