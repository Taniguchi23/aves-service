package com.utpsistemas.distribuidoraavesservice.cliente.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    @Column( name = "tipo_documento", columnDefinition = "INTEGER DEFAULT 1")
    private Integer tipoDocumento = 1;
    @Column( name = "numero_documento", unique = true)
    private String numeroDocumento;
    private String telefono;
    private String apodo;
    @Column( columnDefinition = "CHAR(1) DEFAULT 'A'")
    private Character estado = 'A';
    private String latitud;
    private String longitud;
    private String direccion;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
