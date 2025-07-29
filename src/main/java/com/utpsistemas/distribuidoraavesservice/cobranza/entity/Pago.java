package com.utpsistemas.distribuidoraavesservice.cobranza.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pago")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cobranza_id")
    private Cobranza cobranza;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_pago_id")
    private TipoPago tipoPago;

    @ManyToOne
    @JoinColumn(name = "forma_pago_id")
    private FormaPago formaPago; // solo aplica si tipo = INGRESO

    @Column(nullable = false)
    private BigDecimal monto;

    private String motivo;

    @Column(nullable = false, length = 1)
    private Character estado = 'A'; // 'A' = Activo, 'I' = Inactivo

    @Column(nullable = false)
    private LocalDate fecha;
}
