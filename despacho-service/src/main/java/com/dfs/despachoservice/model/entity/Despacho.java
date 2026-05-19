package com.dfs.despachoservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "despachos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Despacho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long pedidoId;
    private String direccionDestino;
    private String estado; // PENDIENTE, EN_RUTA, ENTREGADO
    private String transportista;
    private String rutaOptimizada;
}
