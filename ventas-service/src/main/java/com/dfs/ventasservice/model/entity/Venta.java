package com.dfs.ventasservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ventas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime fecha;
    private Long clienteId;
    private Double total;
    private Double descuento;
    private String tipoDocumento; // BOLETA, FACTURA
    private String estado; // COMPLETADA, DEVUELTA
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles;
}
