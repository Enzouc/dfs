package com.dfs.sucursalservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sucursales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String direccion;
    private String ciudad;
    private String horario;
    private String personalEncargado;
}
