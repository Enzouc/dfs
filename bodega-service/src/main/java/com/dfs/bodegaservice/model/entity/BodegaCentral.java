package com.dfs.bodegaservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bodega_central")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodegaCentral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long productoId;
    private Integer stockCentral;
    private Integer stockMinimo;
    private String ubicacion;
}
