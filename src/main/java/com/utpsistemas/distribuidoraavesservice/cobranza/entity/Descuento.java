package com.utpsistemas.distribuidoraavesservice.cobranza.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "descuentos")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Descuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cobranza_id")
    private Cobranza cobranza;

    private Character estado;
}
