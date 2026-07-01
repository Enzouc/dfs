package com.dfs.carritoservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items_carrito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "nombre_producto")
    private String nombreProducto;

    @Column(name = "precio_unitario")
    private Double precioUnitario;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "subtotal")
    private Double subtotal;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            this.subtotal = precioUnitario * cantidad;
        } else {
            this.subtotal = 0.0;
        }
    }
}
