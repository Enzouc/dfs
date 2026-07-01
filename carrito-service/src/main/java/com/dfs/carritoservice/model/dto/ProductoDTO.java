package com.dfs.carritoservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stockActual;
}
