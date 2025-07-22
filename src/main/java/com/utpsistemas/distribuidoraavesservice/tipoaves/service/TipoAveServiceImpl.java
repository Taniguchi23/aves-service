package com.utpsistemas.distribuidoraavesservice.tipoaves.service;

import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import com.utpsistemas.distribuidoraavesservice.tipoaves.dto.TipoAveRequest;
import com.utpsistemas.distribuidoraavesservice.tipoaves.dto.TipoAveResponse;
import com.utpsistemas.distribuidoraavesservice.tipoaves.entity.TipoAve;
import com.utpsistemas.distribuidoraavesservice.tipoaves.mapper.TipoAveMapper;
import com.utpsistemas.distribuidoraavesservice.tipoaves.repository.TipoAveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoAveServiceImpl implements TipoAveService {

    @Autowired
    private TipoAveRepository tipoAveRepository;

    @Autowired
    private TipoAveMapper mapper;

    @Override
    public List<TipoAveResponse> listarTipoAve() {
        return tipoAveRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public TipoAveResponse guardarTipoAve(TipoAveRequest tipoAveRequest) {
        if (tipoAveRepository.existsByNombreIgnoreCase(tipoAveRequest.nombre())) {
            throw new ApiException("Ya existe un tipo de ave con el nombre: " + tipoAveRequest.nombre(), HttpStatus.CONFLICT);
        }

        TipoAve tipoAve = mapper.toEntity(tipoAveRequest);
        return mapper.toResponse(tipoAveRepository.save(tipoAve));
    }

    @Override
    public TipoAveResponse actualizarTipoAve(TipoAveRequest tipoAveRequest) {
        if (tipoAveRequest.id() == null)
            throw new ApiException("El ID es obligatorio para actualizar", HttpStatus.BAD_REQUEST);

        TipoAve existente = tipoAveRepository.findById(tipoAveRequest.id())
                .orElseThrow(() -> new ApiException("Tipo de ave no encontrado con ID: " + tipoAveRequest.id(), HttpStatus.NOT_FOUND));

        boolean nombreDuplicado = tipoAveRepository.existsByNombreIgnoreCase(tipoAveRequest.nombre())
                && !existente.getNombre().equalsIgnoreCase(tipoAveRequest.nombre());

        if (nombreDuplicado)
            throw new ApiException("Ya existe otro tipo de ave con el nombre: " + tipoAveRequest.nombre(),HttpStatus.CONFLICT);

        existente.setNombre(tipoAveRequest.nombre());
        return mapper.toResponse(tipoAveRepository.save(existente));
    }
}
