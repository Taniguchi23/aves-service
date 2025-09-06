package com.utpsistemas.distribuidoraavesservice.tipoaves.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_aves")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TipoAve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private float conTripaKg;
    private float sinTripaKg;
    private float noAplicaKg;
}
