package com.utpsistemas.distribuidoraavesservice.estado.service;

import com.utpsistemas.distribuidoraavesservice.estado.dto.EstadoResponse;
import com.utpsistemas.distribuidoraavesservice.estado.mapper.EstadoMapper;
import com.utpsistemas.distribuidoraavesservice.estado.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoServiceImpl implements EstadoService {

    @Autowired
    private EstadoRepository estadoRepository;

    @Autowired
    private EstadoMapper estadoMapper;

    @Override
    public List<EstadoResponse> listaEstado() {
        return estadoRepository.findAll()
                .stream()
                .map(estadoMapper::toEstadoResponse) // Entidad → DTO
                .toList();
    }
}
