package com.utpsistemas.distribuidoraavesservice.merma.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "merma")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Merma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private float porcentaje;
}
