package com.utpsistemas.distribuidoraavesservice.cobranza.seed;

import com.utpsistemas.distribuidoraavesservice.cobranza.entity.PedidoCobranzaMovimientoCategoria;
import com.utpsistemas.distribuidoraavesservice.cobranza.enums.TipoPedidoCobranzaMovimiento;
import com.utpsistemas.distribuidoraavesservice.cobranza.repository.CobranzaMovimientoCategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SeedPedidoCobranzaMovimientoCategoria {

    private final CobranzaMovimientoCategoriaRepository repo;

    @Bean
    CommandLineRunner seedCategoriasCobranza() {
        return args -> {
            crearSiNoExiste("Peso Diferente", "Pago en efectivo", TipoPedidoCobranzaMovimiento.DESCUENTO);
            crearSiNoExiste("Pollo deñado", "Pollo deñado", TipoPedidoCobranzaMovimiento.DESCUENTO);
            crearSiNoExiste("Precio Especial", "Precio Especial", TipoPedidoCobranzaMovimiento.DESCUENTO);
            crearSiNoExiste("Otros", "Otros", TipoPedidoCobranzaMovimiento.DESCUENTO);
            crearSiNoExiste("Efectivo", "Efectivo", TipoPedidoCobranzaMovimiento.PAGO);
            crearSiNoExiste("Transferencia", "Transferencia", TipoPedidoCobranzaMovimiento.PAGO);
        };
    }

    private void crearSiNoExiste(String nombre, String descripcion, TipoPedidoCobranzaMovimiento tipo) {
        if (!repo.existsByNombreIgnoreCase(nombre)) {
            var cat = new PedidoCobranzaMovimientoCategoria();
            cat.setNombre(nombre);
            cat.setDescripcion(descripcion);
            cat.setTipo(tipo);
            cat.setEstado('A');
            repo.save(cat);
        }
    }
}
