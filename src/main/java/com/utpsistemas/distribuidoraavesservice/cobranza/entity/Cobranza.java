package com.utpsistemas.distribuidoraavesservice.cobranza.entity;

import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cobranza")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cobranza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(name = "monto_total", nullable = false)
    private BigDecimal montoTotal;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 20)
    private String estado; // Pendiente, Parcial, Pagado
}