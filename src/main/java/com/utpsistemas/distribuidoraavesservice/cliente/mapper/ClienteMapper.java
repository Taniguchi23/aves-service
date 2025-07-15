package com.utpsistemas.distribuidoraavesservice.cliente.mapper;

import com.utpsistemas.distribuidoraavesservice.auth.helper.Hashid;
import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteRequest;
import com.utpsistemas.distribuidoraavesservice.cliente.dto.ClienteResponse;
import com.utpsistemas.distribuidoraavesservice.cliente.entity.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    @Autowired
    private Hashid hashid;

    public ClienteResponse clienteToClienteResponse(Cliente cliente) {
        return new ClienteResponse(
                hashid.encode(cliente.getId()),
                cliente.getNombres(),
                cliente.getTipoDocumento(),
                cliente.getNumeroDocumento(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.getApodo(),
                cliente.getLatitud(),
                cliente.getLongitud(),
                cliente.getEstado()
        );
    }

    public Cliente clienteRequestToCliente(ClienteRequest request) {
       Cliente cliente = new Cliente();
       if (request.id() != null) cliente.setId(hashid.decode(request.id()));
       cliente.setNombres(request.nombres());
       if (request.tipoDocumento() != null) cliente.setTipoDocumento(request.tipoDocumento());
       if (request.estado() != null) cliente.setEstado(request.estado());
       cliente.setNumeroDocumento(request.numeroDocumento());
       cliente.setTelefono(request.telefono());
       cliente.setApodo(request.apodo());
       cliente.setLatitud(request.latitud());
       cliente.setLongitud(request.longitud());
       cliente.setDireccion(request.direccion());
       return cliente;
    }
}
