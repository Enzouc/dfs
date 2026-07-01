package com.dfs.carritoservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "subtotal")
    private Double subtotal;

    @Column(name = "impuestos")
    private Double impuestos;

    @Column(name = "total")
    private Double total;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemCarrito> items = new ArrayList<>();

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public void addItem(ItemCarrito item) {
        items.add(item);
        item.setCarrito(this);
    }

    public void removeItem(ItemCarrito item) {
        items.remove(item);
        item.setCarrito(null);
    }
}
