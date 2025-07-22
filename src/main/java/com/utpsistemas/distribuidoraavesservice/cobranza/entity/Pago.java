package com.utpsistemas.distribuidoraavesservice.cobranza.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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

    @Column(name = "monto_cobrado", nullable = false)
    private BigDecimal montoCobrado;

    private String forma;
    private String observacion;

    @Column(nullable = false)
    private Character estado;
}
