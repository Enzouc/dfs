package com.dfs.ventasservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_ventas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long productoId;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
