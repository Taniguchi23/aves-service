package com.utpsistemas.distribuidoraavesservice.pedido.controller;

import com.utpsistemas.distribuidoraavesservice.pedido.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("aves-service/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;


}
