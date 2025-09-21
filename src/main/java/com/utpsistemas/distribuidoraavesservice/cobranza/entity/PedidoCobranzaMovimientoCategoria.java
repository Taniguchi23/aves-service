package com.utpsistemas.distribuidoraavesservice.cobranza.entity;

import com.utpsistemas.distribuidoraavesservice.cobranza.enums.TipoPedidoCobranzaMovimiento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedidos_cobranza_movimientos_categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCobranzaMovimientoCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPedidoCobranzaMovimiento tipo;

    @Column(nullable = false, length = 1)
    private Character estado = 'A'; // A = Activo, I = Inactivo
}
