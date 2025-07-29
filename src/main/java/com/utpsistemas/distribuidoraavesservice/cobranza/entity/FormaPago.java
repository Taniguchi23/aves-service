package com.utpsistemas.distribuidoraavesservice.cobranza.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "forma_pago")
@Data
public class FormaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false, length = 1)
    private Character estado = 'A';
}
