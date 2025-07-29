package com.utpsistemas.distribuidoraavesservice.cobranza.service;

import com.utpsistemas.distribuidoraavesservice.auth.exception.ApiException;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.PagoRequest;
import com.utpsistemas.distribuidoraavesservice.cobranza.dto.PagoResponse;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Cobranza;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.FormaPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.Pago;
import com.utpsistemas.distribuidoraavesservice.cobranza.entity.TipoPago;
import com.utpsistemas.distribuidoraavesservice.cobranza.mapper.PagoMapper;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.CobranzaRepository;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.FormaPagoRepository;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.PagoRepository;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.TipoPagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoServiceImpl implements PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private TipoPagoRepository tipoPagoRepository;

    @Autowired
    private FormaPagoRepository formaPagoRepository;

    @Autowired
    private PagoMapper pagoMapper;

    @Autowired
    private CobranzaRepository cobranzaRepository;

    @Override
    public PagoResponse crearPago(PagoRequest pagoRequest) {
        Cobranza cobranza = cobranzaRepository.findById(pagoRequest.cobranzaId())
                .orElseThrow(() -> new ApiException("Cobranza no encontrada", HttpStatus.NOT_FOUND));

        TipoPago tipoPago = tipoPagoRepository.findById(pagoRequest.tipoPagoId())
                .orElseThrow(() -> new ApiException("Tipo de pago no válido", HttpStatus.NOT_FOUND));

        FormaPago formaPago = null;
        if ("PAGO".equalsIgnoreCase(tipoPago.getNombre())) {
            if (pagoRequest.formaPagoId() == null) {
                throw new ApiException("La forma de pago es obligatoria para tipo PAGO", HttpStatus.NOT_FOUND);
            }
            formaPago = formaPagoRepository.findById(pagoRequest.formaPagoId())
                    .orElseThrow(() -> new ApiException("Forma de pago no válida", HttpStatus.NOT_FOUND));

        }

        Pago pago = new Pago();
        pago.setCobranza(cobranza);
        pago.setTipoPago(tipoPago);
        pago.setFormaPago(formaPago);
        pago.setMonto(pagoRequest.monto());
        pago.setMotivo(pagoRequest.motivo());
        pago.setFecha(pagoRequest.fecha());
        pago.setEstado('A');

        pago = pagoRepository.save(pago);

        return pagoMapper.toResponse(pago);
    }

    @Override
    public List<Pago> obtenerPagosActivosPorCobranza(Long cobranzaId) {
        return pagoRepository.findAllActivosByCobranzaId(cobranzaId);
    }


}
