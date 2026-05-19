package com.dfs.soporteservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "soporte_reseñas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketSoporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long clienteId;
    private String tipo; // SOPORTE, RESEÑA
    private Long productoId; // Solo para reseñas
    private String mensaje;
    private Integer calificacion; // 1-5
    private String estado; // ABIERTO, CERRADO
    private LocalDateTime fecha;
}
