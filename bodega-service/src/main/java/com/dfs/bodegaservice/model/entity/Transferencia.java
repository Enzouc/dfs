package com.dfs.bodegaservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transferencias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transferencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long productoId;
    private Integer cantidad;
    private Long sucursalOrigenId;
    private Long sucursalDestinoId;
    private String estado; // SOLICITADA, APROBADA, RECHAZADA, EN_TRANSITO
}
