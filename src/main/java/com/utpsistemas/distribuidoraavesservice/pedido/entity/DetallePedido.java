package com.utpsistemas.distribuidoraavesservice.pedido.entity;

import com.utpsistemas.distribuidoraavesservice.pedido.enums.TipoMerma;
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
@Table(name = "pedido_detalle")
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "tipo_ave_id")
    private TipoAve tipoAve;

    private Integer cantidadPollo;
    private BigDecimal peso;
    private BigDecimal precioXKilo;
    private BigDecimal montoEstimado;

    private float mermaKg;
    private boolean opDirecta;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private TipoMerma tipoMerma;

    private Integer estado;
}
