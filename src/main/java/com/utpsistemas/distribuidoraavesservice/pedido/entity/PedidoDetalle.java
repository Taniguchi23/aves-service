package com.utpsistemas.distribuidoraavesservice.pedido.entity;


import com.utpsistemas.distribuidoraavesservice.tipoaves.entity.TipoAve;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "pedidos_detalle")
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "tipo_ave_id")
    private TipoAve tipoAve;

    private Integer cantidad;
    @Column(name = "peso_base", precision = 19, scale = 2)
    private BigDecimal pesoBase; //sin merma
    private BigDecimal peso;
    private BigDecimal precioXKilo;
    private BigDecimal montoEstimado;

    @Column(name = "merma_kg", precision = 19, scale = 2)
    private BigDecimal mermaKg;

    @Column(name = "importe_subtotal", precision = 19, scale = 2)
    private BigDecimal importeSubTotal;

    private Boolean opDirecta;

    @Column(length = 10)
    private String tipoMerma;

    private Integer estado;
}
