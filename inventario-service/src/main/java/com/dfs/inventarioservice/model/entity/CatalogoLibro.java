package com.dfs.inventarioservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "catalogo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogoLibro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String autor;
    private String editorial;
    private String genero;
    private Double precio;
    private String descripcion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;
}
