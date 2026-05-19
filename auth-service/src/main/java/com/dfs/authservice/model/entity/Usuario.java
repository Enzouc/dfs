package com.dfs.authservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 20, message = "El username debe tener entre 4 y 20 caracteres")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Column(nullable = false)
    private String rol; // ADMIN, JEFE_SUCURSAL, ASISTENTE_VENTAS, LOGISTICA, BODEGA, CLIENTE

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> permisos;
}
