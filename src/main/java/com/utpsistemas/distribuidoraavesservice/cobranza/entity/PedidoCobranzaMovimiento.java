package com.utpsistemas.distribuidoraavesservice.cobranza.entity;

import com.utpsistemas.distribuidoraavesservice.cobranza.enums.TipoPedidoCobranzaMovimiento;
import com.utpsistemas.distribuidoraavesservice.pedido.entity.Pedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos_cobranza_movimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCobranzaMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Pedido
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;


    @Column(nullable = false)
    private Integer  tipo;

    // Relación con categoría
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private PedidoCobranzaMovimientoCategoria categoria;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    private String observacion;

    @Column(nullable = false, length = 1)
    private Character estado = 'A'; // A = Activo, I = Inactivo

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;
}